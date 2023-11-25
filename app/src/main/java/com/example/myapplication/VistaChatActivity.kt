package com.example.myapplication

import MessageAdapter
import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityVistaChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VistaChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVistaChatBinding
    var dbRef2: DatabaseReference?=null
    private lateinit var messageAdapter: MessageAdapter
    private var messageList = ArrayList<Message>()
    val CHANNEL_ID = "com.example.myapplication"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVistaChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val data = intent.getStringExtra("extra_data")


        val parcheRef = FirebaseDatabase.getInstance().getReference("Parche").child(data.toString())

        parcheRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val nombreParche = dataSnapshot.child("nombre").getValue(String::class.java)
                    val descripcionParche = dataSnapshot.child("parcheDescription").getValue(String::class.java)
                    binding.nombrechat.text = nombreParche ?: "Nombre no disponible"
                    binding.descripcion.text= descripcionParche ?: "Descripcion NO encontrada"
                } else {
                    // El parche con ese ID no existe o no se pudo obtener el nombre
                    binding.nombrechat.text = "Parche no encontrado"
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error
            }
        })

        var nombre: String? = ""

        dbRef2 = FirebaseDatabase.getInstance().getReference("Parche").child(data.toString()).child("mensaje")
        var usuarioActual = FirebaseAuth.getInstance().currentUser?.uid

        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(usuarioActual.toString())

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Suponiendo que el nombre del usuario se almacena en un campo llamado "nombre"
                nombre = snapshot.child("nombre").getValue(String::class.java)
                if (nombre != null) {
                    // Haz algo con el nombre del usuario
                    Log.d("NombreUsuario", "Nombre del usuario: $nombre")
                } else {
                    // Manejar el caso en que el nombre no esté disponible
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error
            }
        })

        // Cargar los mensajes del chat en el recycler view
        // Inicializar RecyclerView y su adaptador
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(this, messageList)
        binding.recyclerView.adapter = messageAdapter

        val data2 = intent.getStringExtra("extra_data") ?: return
        dbRef2 = FirebaseDatabase.getInstance().getReference("Parche").child(data2).child("mensaje")

        // Leer los mensajes y escuchar cambios
        dbRef2!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messageList.clear() // Limpiar la lista para evitar duplicados
                for (snapshot in dataSnapshot.children) {
                    val message = snapshot.getValue(Message::class.java)
                    message?.let { messageList.add(it) }
                }
                messageAdapter.notifyDataSetChanged() // Notificar al adaptador del cambio
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error
            }
        })


        //val dbRef3 = FirebaseDatabase.getInstance().getReference("Parche").child(data.toString()).child("mensaje").child("user")

        binding.send.setOnClickListener {
            val messageText = binding.msg.text.toString()
            if (messageText.isNotEmpty() && nombre != null) {
                val chatMessage = Message(messageText, nombre!!)
                dbRef2!!.push().setValue(chatMessage)
            }

            val mBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(
                    applicationContext,
                    CHANNEL_ID
                )
            mBuilder.setContentTitle("Discotec")
                .setSmallIcon(R.drawable.icono_reservas)
                .setContentText(messageText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val intent = Intent(applicationContext,
                VistaChatActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE)
// Set the intent that fires when the user taps the
// notification.
            mBuilder.setContentIntent(pendingIntent)
// Removes notification when touching it
            mBuilder.setAutoCancel(true)
            val notifId = 10
            val notificationManager =
                NotificationManagerCompat.from(applicationContext)
            if (ContextCompat.checkSelfPermission(applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(notifId, mBuilder.build())
            }
        }

        binding.back.setOnClickListener {
            val intent = Intent(applicationContext, ChatMenuActivity::class.java)
            startActivity(intent)
        }
    }
}
