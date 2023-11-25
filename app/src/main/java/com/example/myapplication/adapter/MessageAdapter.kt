import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Message
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MessageAdapter(private val context: Context, private val messageList: ArrayList<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1
    private val firebaseUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val msgType = getItemViewType(viewType)
        val layout = if (msgType == MESSAGE_TYPE_LEFT) R.layout.item_left else R.layout.item_right
        val view1 = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ViewHolder(view1)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message: Message = messageList[position]
        holder.messageText.text = message.message
        holder.messageUser.text = message.userName
        // Puedes añadir más lógica aquí si necesitas
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.tvMessage)
        val messageUser: TextView = view.findViewById(R.id.usuario)
        // Añade más vistas aquí si es necesario
    }

    override fun getItemViewType(position: Int): Int {
        var nombre: String? = ""
        var usuarioActual = FirebaseAuth.getInstance().currentUser?.uid
        val message = messageList[position]

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

        return if (message.userName == nombre) {
            MESSAGE_TYPE_RIGHT // Mensajes del usuario actual
        } else {
            MESSAGE_TYPE_LEFT // Mensajes de otros usuarios
        }
    }

}
