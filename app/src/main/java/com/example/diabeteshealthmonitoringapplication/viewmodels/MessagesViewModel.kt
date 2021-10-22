package com.example.diabeteshealthmonitoringapplication.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.diabeteshealthmonitoringapplication.models.Chat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val TAG = "MessagesViewModel"
class MessagesViewModel(application:Application):AndroidViewModel(application) {
    var chats:MutableLiveData<List<Chat>> = MutableLiveData()
    var chatList: ArrayList<Chat> = ArrayList()
    fun getMessages(myUid:String,hisUid:String):LiveData<List<Chat>>{
        FirebaseDatabase.getInstance().getReference("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val chat = it.getValue(Chat::class.java)
                        if (chat!=null){
                            if (chat.fromUid==myUid && chat.toUid==hisUid || chat.toUid==myUid && chat.fromUid==hisUid){
                                chatList.add(chat)
                            }
                        }
                        chats.postValue(chatList);
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "onCancelled: ${error.message}" )
                }
            })
        return chats
    }
}