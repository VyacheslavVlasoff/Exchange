package com.example.myapplication

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.core.view.get
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class ChangeProductActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private var mCropImageUri: Uri? = null
    private var imageStroke: String = ""
    private var imageKey: String = ""

    private lateinit var imgProductChange: ImageView
    private lateinit var btnChangeProduct: Button
    private lateinit var nameProduct: EditText
    private lateinit var type: Spinner
    private lateinit var costProduct: EditText
    private lateinit var typeProduct: Spinner
    private lateinit var location: AutoCompleteTextView
    private lateinit var description: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_product)

        supportActionBar?.setBackgroundDrawable(resources.getDrawable(R.color.gradColor1))
        supportActionBar?.title = intent.getStringExtra("name")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        imgProductChange = findViewById(R.id.imgProductChange)
        btnChangeProduct = findViewById(R.id.btnChangeThisProduct)
        nameProduct = findViewById(R.id.etNameProductChange)
        type = findViewById(R.id.spinnerTypesChange)
        costProduct = findViewById(R.id.etCostProductChange)
        typeProduct = findViewById(R.id.spinnerTypesProductChange)
        location = findViewById(R.id.etLocationProductChange)
        description = findViewById(R.id.etDescriptionProductChange)

        imageStroke = intent.getStringExtra("img")!!
        Picasso.get().load(intent.getStringExtra("img")).into(imgProductChange)
        nameProduct.setText(intent.getStringExtra("name"))
        costProduct.setText(intent.getStringExtra("cost"))
        location.setText(intent.getStringExtra("location"))
        description.setText(intent.getStringExtra("description"))

        database = Firebase.database.reference

        val adapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(
            this, R.array.types,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        type.adapter = adapter

        var streets = mutableListOf<String>()
        database.child("Streets").get().addOnCompleteListener {task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                snapshot.children.forEach { street ->
                    snapshot.child(street.key!!).children.forEach {num->
                        streets.add(street.key + ", " +
                                snapshot.child(street.key!!).child(num.key!!).getValue(String::class.java)!!)
                    }
                    //streets.add(snapshot.child(street.key!!).getValue(String::class.java)!!)
                }
            }
        }

        val locationAdapter: ArrayAdapter<*> = ArrayAdapter(this,
            android.R.layout.simple_dropdown_item_1line, streets)
        location.setAdapter(locationAdapter)
        //location.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

        val itemSelectedListener: AdapterView.OnItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    // Получаем выбранный объект
                    costProduct.isEnabled = type.selectedItem == "Продажа"
                    costProduct.text.clear()
                    //costProduct.text.append('0')
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        type.onItemSelectedListener = itemSelectedListener

        imgProductChange.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, 1)
        }

        btnChangeProduct.setOnClickListener {
            CheckFeel(intent.getStringExtra("uid")!!)
            //Toast.makeText(this, "Должно быть изменено", Toast.LENGTH_SHORT).show()
        }
    }

    private fun CheckFeel(uid: String) {
        if (nameProduct.text.toString().isBlank()) {
            Toast.makeText(this, "Название не указано", Toast.LENGTH_SHORT).show()
            return
        }

        if (type.selectedItem == "Продажа" && costProduct.text.toString().isBlank()) {
            Toast.makeText(this, "Цена не указана", Toast.LENGTH_SHORT).show()
            return
        }

        if (location.text.toString().isBlank()) {
            Toast.makeText(this, "Место сделки не указано", Toast.LENGTH_SHORT).show()
            return
        }

        var storage = Firebase.storage
        var storageReference = storage.reference

        var userLog = Firebase.auth.currentUser
        var keyPhoto = UUID.randomUUID().toString();
        imageKey = keyPhoto
        var strName = userLog!!.uid + "/" + imageKey + ".jpg"
        imageStroke = strName;
        storageReference = storageReference.child(strName)

        // Get the data from an ImageView as bytes
        imgProductChange.isDrawingCacheEnabled = true
        imgProductChange.buildDrawingCache()
        val bitmap = (imgProductChange.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Загрузка...")
        progressDialog.show()

        storageReference.putBytes(data)
            .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSn -> // Got the uri
                progressDialog.dismiss()

                //database = Firebase.database.reference
                //storageReference = storageReference.child(imageStroke)

                var cost: String = if (type.selectedItem == "Продажа") costProduct.text.toString() + " руб."
                else type.selectedItem.toString()

                var uriImage = "https://firebasestorage.googleapis.com/v0/b/my-application-f8aff.appspot.com/o/def.jpg?alt=media&token=7e24bbcf-a4d7-4020-b501-94049d30f77d"

                storageReference.downloadUrl
                    .addOnSuccessListener(OnSuccessListener<Uri> { uri -> // Got the uri
                        uriImage = uri.toString()

                        var product = AddProduct(
                            nameProduct.text.toString(), cost,
                            typeProduct.selectedItem.toString(), description.text.toString(),
                            location.text.toString(), uriImage
                        )

                        //database.child("Users").child(userLog!!.uid).child("products").child(oldUrl!!).removeValue()
                        database.child("Users").child(userLog!!.uid).child("products")
                            .child(uid).setValue(product)
                        Toast.makeText(this, "Loaded", Toast.LENGTH_SHORT).show()
                    }).addOnFailureListener(OnFailureListener {
                        Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show()
                    })
                finish()
            }).addOnFailureListener(OnFailureListener {
                // Handle any errors
            }).addOnProgressListener(OnProgressListener<UploadTask.TaskSnapshot> { taskSn ->
                val progress: Double = (100.0
                        * taskSn.bytesTransferred
                        / taskSn.totalByteCount)
                progressDialog.setMessage(
                    "Uploaded "
                            + progress.toInt() + "%"
                )
            })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var bitmap: Bitmap = Bitmap.createBitmap(100, 100,
            Bitmap.Config.ARGB_8888);
        val imageView: ImageView = findViewById(R.id.imgProductChange)

        when (requestCode) {
            1 -> if (resultCode === RESULT_OK) {
                val selectedImage: Uri = data?.data!!
                mCropImageUri = selectedImage
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                    val width = bitmap.width
                    val height = bitmap.height
                    val newWidth = if (height > width) width else height
                    val newHeight =
                        if (height > width) height - (height - width) else height
                    var cropW = (width - height) / 2
                    cropW = if (cropW < 0) 0 else cropW
                    var cropH = (height - width) / 2
                    cropH = if (cropH < 0) 0 else cropH

                    bitmap = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                imageView.setImageBitmap(bitmap)
            }
            100 -> if (resultCode == RESULT_OK) {
                val cropIntent = Intent("com.android.camera.action.CROP")
                // indicate image type and Uri
                cropIntent.setDataAndType(data?.data, "image/*")
                // set crop properties here
                cropIntent.putExtra("crop", true)
                // indicate aspect of desired crop
                cropIntent.putExtra("aspectX", 1)
                cropIntent.putExtra("aspectY", 1)
                // indicate output X and Y
                cropIntent.putExtra("outputX", 128)
                cropIntent.putExtra("outputY", 128)
                // retrieve data on return
                cropIntent.putExtra("return-data", true)
                // start the activity - we handle returning in onActivityResult
                startActivityForResult(cropIntent, 1)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}