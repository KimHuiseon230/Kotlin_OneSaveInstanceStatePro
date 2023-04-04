package com.example.onesaveinstancestatepro

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onesaveinstancestatepro.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// 직접 작성한 reflect.Type
import java.lang.reflect.Type

class MainActivity : AppCompatActivity() {
    val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    lateinit var dataList: MutableList<String>
    lateinit var myAdapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        dataList = mutableListOf<String>()

        //1. 번들에 데이터가 있으면 가져오고 기본 변수에 저장하고 없으면 무시하여 데이터를 가져오지 않는다.
        if (savedInstanceState !== null) {
            // !반드시! 널이 아니기 때문에 꼭 닫아줘야 함
            dataList = savedInstanceState.getStringArrayList("dataList")!!.toMutableList()
        }

        //2. 인텐트를 돌려 받음.
        val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            var result: String = it.data?.getStringExtra("result").toString()
            Log.e("MainActivity", "== ${result}==")

            if (result != null && !result.equals("")) {
                dataList.add(result)
            }
            myAdapter.notifyDataSetChanged()
        }

        //3. myAdapter 와 RecyclerView를 연결. 보여주는 모양을 결정한다.
        val layoutManager = LinearLayoutManager(this)
        binding.mainRecyclerView.layoutManager = layoutManager
        myAdapter = MyAdapter(dataList)
        binding.mainRecyclerView.adapter = myAdapter
        binding.mainRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )

        //4. floating tab 클릭 => 인텐트 요청
        binding.mainFab.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            activityResultLauncher.launch(intent)
        }

        // +++ 5. sharedPrefernce 구현하기 start
        // 5-1 sharedPrefernce 저장하기
        binding.btnShared.setOnClickListener {
            /*
            * 76번 라인 :
                * 공유하는 장치를 불러옴
                * getSharedPreferences : 이름으로 식별되는 공유 환경설정 파일이 여러 개 필요한 경우 이 메서드를 사용
                * Context : 앱의 모든 Context에서 이 메서드를 호출
                * Context.MODE_PRIVATE:해당 앱에서만 읽기, 쓰기 가능
            * commit() : 저장공간에 지속적인 동기를 유지하며 preferences를 작성*/
            val sharedPreferences = getSharedPreferences("dataList", Context.MODE_PRIVATE)
            // gson arraylist를 문자열로 바꿔주는 것
            val editor = sharedPreferences!!.edit()
            val gson = Gson()
            val json: String = gson.toJson(dataList)
            editor.putString("oneMessage", json)
            editor.commit()
            Toast.makeText(this, "sharedPreference 저장", Toast.LENGTH_SHORT).show()
        }
        // 5-2 sharedPrefernce 복구하기
        binding.btnRevert.setOnClickListener {
            val sharedPreference = getSharedPreferences("dataList", Context.MODE_PRIVATE)
            val data = sharedPreference.getString("oneMessage", null)
            val type: Type = object : TypeToken<ArrayList<String>?>() {}.type
            val gson = Gson()
            /// data 를 type으로 바꿔주고 이것을  ArrayList<String>로 형변환 하여 dataList에 집어 넣는다
            dataList = gson.fromJson<Any>(data, type) as ArrayList<String>

            val layoutManager = LinearLayoutManager(this)
            myAdapter = MyAdapter(dataList)
            binding.mainRecyclerView.layoutManager = layoutManager
            binding.mainRecyclerView.adapter = myAdapter
            binding.mainRecyclerView.addItemDecoration(
                DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
            Toast.makeText(this, "sharedPreference 복구", Toast.LENGTH_SHORT).show()
        }
        // +++ 5. sharedPrefernce 구현하기 end


    }// onCreate End

    override fun onBackPressed() {
//        super.onBackPressed()
        intent.putExtra("result","")
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    //++++ 오버라이드 함수 정의 ++++
    //화면을 전환 할 경우에 저장 해야함. 어디서? 번들에...
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("dataList", ArrayList(dataList))
    }

}