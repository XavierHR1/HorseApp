package org.insbaixcamp.lazycolumn2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlin.jvm.java
import org.insbaixcamp.lazycolumn2.data.Horse

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Connectar amb Firestore
        val db = FirebaseFirestore.getInstance()

        // 2. Afegir tres cavalls si la col·lecció és buida
        afegirCavallsInicials(db)

        setContent {
            MaterialTheme {
                PantallaCavalls(db)
            }
        }
    }

    private fun afegirCavallsInicials(db: FirebaseFirestore) {
        val collection = db.collection("horses")

        // Comprovar si ja existeixen cavalls al servidor
        collection.limit(1).get(Source.SERVER)
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    val horse1 = Horse(
                        name = "Spirit",
                        age = 5,
                        breed = "Àrab",
                        weight = 450.0,
                        topSpeed = 60.0
                    )

                    val horse2 = Horse(
                        name = "Thunder",
                        age = 8,
                        breed = "Pura sang",
                        weight = 520.0,
                        topSpeed = 68.0
                    )

                    val horse3 = Horse(
                        name = "Luna",
                        age = 3,
                        breed = "Andalús",
                        weight = 480.0,
                        topSpeed = 55.0
                    )

                    // Crear els tres documents en una sola operació
                    val batch = db.batch()
                    batch.set(collection.document("horse_1"), horse1)
                    batch.set(collection.document("horse_2"), horse2)
                    batch.set(collection.document("horse_3"), horse3)

                    batch.commit()
                        .addOnSuccessListener {
                            Log.d("HorseApp", "S'han creat els tres cavalls")
                        }
                        .addOnFailureListener { error ->
                            Log.e("HorseApp", "Error en guardar cavalls", error)
                        }
                }
            }
            .addOnFailureListener { error ->
                Log.e("HorseApp", "Error en consultar Firestore", error)
            }
    }
}

@Composable
fun PantallaCavalls(db: FirebaseFirestore) {

    // 3. ArrayList observable per Jetpack Compose
    var cavalls by remember {
        mutableStateOf(arrayListOf<Horse>())
    }

    var carregant by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Rebre els documents de Firestore i els seus canvis
    DisposableEffect(db) {
        val listener = db.collection("horses")
            .addSnapshotListener { documents, exception ->
                carregant = false

                if (exception != null) {
                    error = exception.message
                } else {
                    val llista = arrayListOf<Horse>()

                    documents?.documents?.forEach { document ->
                        val horse = document.toObject(Horse::class.java)
                        if (horse != null) {
                            llista.add(horse)
                        }
                    }

                    // Assignar una llista nova perquè Compose detecti el canvi
                    cavalls = llista
                    error = null
                }
            }

        // Aturar l'escolta quan la pantalla desapareix
        onDispose {
            listener.remove()
        }
    }

    // 4. Mostrar els cavalls en una LazyColumn
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Llistat de cavalls",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            carregant -> {
                CircularProgressIndicator()
            }

            error != null -> {
                Text("Error:$error")
            }

            cavalls.isEmpty() -> {
                Text("No hi ha cavalls")
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = cavalls,
                        key = { it.id }
                    ) { horse ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = horse.name,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text("Edat:${horse.age} anys")
                                Text("Raça:${horse.breed}")
                                Text("Pes:${horse.weight} kg")
                                Text("Velocitat:${horse.topSpeed} km/h")
                            }
                        }
                    }
                }
            }
        }
    }
}