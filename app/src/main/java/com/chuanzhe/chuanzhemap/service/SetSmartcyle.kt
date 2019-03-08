package com.chuanzhe.chuanzhemap.service

import android.app.IntentService
import android.content.Intent

/**
 * Created by  yingke on 2019-03-06.
 * yingke.github.io
 */
@Suppress("UNREACHABLE_CODE")
class SetSmartcyle: IntentService("SetSmartcyle") {
    override fun onHandleIntent(intent: Intent?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.


    }


//

  //  private  fun getqiandaobyid(item :PointItems){
//        var bmobQuery: BmobQuery<Qiandao> = BmobQuery()
//
//        bmobQuery.addWhereEqualTo("items" , BmobPointer(item))
//        bmobQuery.findObjects(object : FindListener<Qiandao>(){
//            override fun done(p0: MutableList<Qiandao>?, p1: BmobException?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                if (p0?.size!! >= 2){
//                    var q1 = p0?.get(0)
//                    var q2 = p0?.get(1)
//
//
//                    var last = (q1.buhuoliang+q2.cunhuoliang) - q1.cunhuoliang
//                    var riqi = C.getDistanceTime(q1.updatedAt,q2.updatedAt)
//                    if (riqi.toInt() ==0){
//                       riqi = 1
//                    }
//
//                   var Dailysales = last / riqi
//                    Log.i("smart",Dailysales.toString())
//
//
//                }
//
//
//
//            }
//        })
    //}
}