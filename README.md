# HorseApp 🐴

**HorseApp** és una aplicació Android desenvolupada amb **Kotlin** i **Jetpack Compose** que es connecta amb **Google Cloud Firestore** per gestionar i mostrar un llistat de cavalls en temps real.

---

## 🛠️ Tecnologies utilitzades

- **Llenguatge:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) amb **Material Design 3**
- **Base de dades en temps real:** [Google Cloud Firestore](https://firebase.google.com/docs/firestore) (Firebase SDK)
- **Gestió de construcció:** Gradle (Kotlin DSL `.gradle.kts`) i Version Catalog (`libs.versions.toml`)
- **Arquitectura & Desenvolupament:**
  - `DisposableEffect` i `State` (`mutableStateOf`) per a la reactivitat UI/Firestore.
  - Inserció atòmica mitjançant `WriteBatch`.
  - Mapeig automàtic de documents Firestore a objectes de dades Kotlin amb `@set:DocumentId`.

---

## 📋 Què s'ha fet

1. **Model de dades (`Horse`):**
   - Creat el data class `Horse` amb les propietats: `id` (annotada amb `@set:DocumentId`), `name`, `age`, `breed`, `weight`, `topSpeed`, `createdAt` i `updatedAt`.

2. **Connexió amb Firestore (`MainActivity`):**
   - Inicialitzada la instància de Firestore (`FirebaseFirestore.getInstance()`).

3. **Inserció automàtica de dades inicials (`afegirCavallsInicials`):**
   - Comprovació si la col·lecció `horses` conté registres previs.
   - En cas d'estar buida, creació en una sola operació *batch* de 3 cavalls d'exemple (`Spirit`, `Thunder` i `Luna`).

4. **Escolta de dades en temps real (`PantallaCavalls`):**
   - Ús de `addSnapshotListener` dins d'un `DisposableEffect` per sincronitzar en temps real els cavalls de la col·lecció `horses` amb un `ArrayList<Horse>` observable.

5. **Interfície d'usuari reactiva amb Jetpack Compose:**
   - Disseny amb `Column`, `Card`, `CircularProgressIndicator` i `LazyColumn` per renderitzar de manera eficient la llista de cavalls amb les seves propietats (Nom, Edat, Raça, Pes i Velocitat màxima).
