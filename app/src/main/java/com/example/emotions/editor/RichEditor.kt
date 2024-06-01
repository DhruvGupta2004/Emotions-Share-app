package com.example.emotions.editor

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.emotions.MainActivity
import com.example.emotions.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RichEditor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rich_editor)


        val boldButton = findViewById<ImageButton>(R.id.boldButton)
        val italicButton = findViewById<ImageButton>(R.id.italicButton)
        val bigTextButton = findViewById<ImageButton>(R.id.bigTextButton)
        val smallTextButton = findViewById<ImageButton>(R.id.smallTextButton)
        val highlightButton = findViewById<ImageButton>(R.id.highlightButton)
        val linkButton = findViewById<ImageButton>(R.id.linkButton)
        val addImageButton = findViewById<ImageButton>(R.id.addImageButton)

        val Title = findViewById<EditText>(R.id.Title)
        val editText = findViewById<EditText>(R.id.editTextText)
        val Post = findViewById<Button>(R.id.post)


        boldButton.setOnClickListener { toggleStyle(Typeface.BOLD) }
        italicButton.setOnClickListener { toggleStyle(Typeface.ITALIC) }
        bigTextButton.setOnClickListener { toggleSize(1.5f) }
        smallTextButton.setOnClickListener { toggleSize(0.75f) }
        highlightButton.setOnClickListener { toggleHighlight() }
        linkButton.setOnClickListener { promptForLink() }
        addImageButton.setOnClickListener { promptForImage() }
        Post.setOnClickListener{
            postToFirebase(Title.text.toString(), SpannableStringBuilder(editText.text.toString()))
            val intent = Intent(this , MainActivity::class.java)
            startActivity(intent)
        }




        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Ensure newly typed text has the same style as the existing text
                if (s != null) {
                    val lastStyle = getLastStyle(s)
                    s.setSpan(lastStyle, 0, s.length, 0)
                }
            }
        })
    }



    private fun getLastStyle(text: Editable): Any {
        val spans = text.getSpans(0, text.length, Any::class.java)
        return if (spans.isNotEmpty()) spans[spans.size - 1] else StyleSpan(Typeface.NORMAL)
    }




    private fun toggleStyle(style: Int) {
        val editText = findViewById<EditText>(R.id.editTextText)
        val start = editText.selectionStart
        val end = editText.selectionEnd
        val spannableString = SpannableStringBuilder(editText.text)

        // Check if the selected text already has the style applied
        val existingStyles = editText.text.getSpans(start, end, StyleSpan::class.java)

        // If the selected text has the style applied, remove it; otherwise, apply it
        if (existingStyles.isNotEmpty()) {
            for (existingStyle in existingStyles) {
                if (existingStyle.style == style) {
                    spannableString.removeSpan(existingStyle)
                } else {
                    spannableString.setSpan(StyleSpan(style), start, end, 0)
                }
            }
        } else {
            spannableString.setSpan(StyleSpan(style), start, end, 0)
        }

        editText.text = spannableString
        editText.setSelection(start, end)
    }



    private fun toggleSize(size: Float) {
        val editText = findViewById<EditText>(R.id.editTextText)
        val start = editText.selectionStart
        val end = editText.selectionEnd
        val spannableString = SpannableStringBuilder(editText.text)

        spannableString.setSpan(RelativeSizeSpan(size), start, end, 0)

        editText.text = spannableString
        editText.setSelection(start, end)
    }



    private fun toggleHighlight() {
        val editText = findViewById<EditText>(R.id.editTextText)
        val start = editText.selectionStart
        val end = editText.selectionEnd
        val highlightColor = ContextCompat.getColor(this, R.color.highlightColor)
        val spannableString = SpannableStringBuilder(editText.text)
        val existingHighlight = editText.text.getSpans(start, end, BackgroundColorSpan::class.java)

        if (existingHighlight.isNotEmpty() && existingHighlight[0].backgroundColor == highlightColor) {
            // Remove the highlight if it's already applied
            spannableString.removeSpan(existingHighlight[0])
        } else {
            // Apply the highlight if it's not applied
            spannableString.setSpan(BackgroundColorSpan(highlightColor), start, end, 0)
        }

        editText.text = spannableString
        editText.setSelection(start, end)
    }



    private fun promptForLink() {
        // Here you would prompt the user for the link URL and apply it to the selected text.
        Toast.makeText(this, "Link functionality not implemented", Toast.LENGTH_SHORT).show()
    }



    private fun promptForImage() {
        // Here you would prompt the user to select an image and insert it into the EditText.
        Toast.makeText(this, "Image functionality not implemented", Toast.LENGTH_SHORT).show()
    }


    private fun postToFirebase(title: String, content: SpannableStringBuilder) {
        // Get the current user's ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Check if the user is authenticated
        if (userId != null) {
            // Convert the SpannableStringBuilder to HTML markup
            val htmlContent = convertToHtml(content)

            // Get a reference to your Firebase database
            val database = Firebase.database

            // Get a reference to a specific node in your database (e.g., "posts")
            val userPostsRef = database.getReference("users").child(userId).child("posts")

            // Create a new unique key for the post
            val newPostRef = userPostsRef.push()

            // Create a HashMap to store the post data
            val postData = hashMapOf(
                "title" to title,
                "content" to htmlContent
            )

            // Set the value of the new post with the data
            newPostRef.setValue(postData)
                .addOnSuccessListener {
                    // Post saved successfully
                    Toast.makeText(this, "Post saved to Firebase", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Failed to save post
                    Toast.makeText(this, "Failed to save post: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // User is not authenticated
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertToHtml(content: SpannableStringBuilder): String {
        var htmlContent = content.toString()

        // Replace bold spans with <b> tags
        content.getSpans(0, content.length, StyleSpan::class.java).forEach { span ->
            if (span.style == Typeface.BOLD) {
                val start = content.getSpanStart(span)
                val end = content.getSpanEnd(span)
                htmlContent = htmlContent.replaceRange(start, end, "<b>${content.subSequence(start, end)}</b>")
            }
        }

        // Replace italic spans with <i> tags
        content.getSpans(0, content.length, StyleSpan::class.java).forEach { span ->
            if (span.style == Typeface.ITALIC) {
                val start = content.getSpanStart(span)
                val end = content.getSpanEnd(span)
                htmlContent = htmlContent.replaceRange(start, end, "<i>${content.subSequence(start, end)}</i>")
            }
        }

        // Replace relative size spans with <span> tags with style attribute
        content.getSpans(0, content.length, RelativeSizeSpan::class.java).forEach { span ->
            val start = content.getSpanStart(span)
            val end = content.getSpanEnd(span)
            val size = span.sizeChange
            val htmlTag = "<span style=\"font-size:${size}em\">${content.subSequence(start, end)}</span>"
            htmlContent = htmlContent.replaceRange(start, end, htmlTag)
        }

        // Replace highlight spans with <span> tags with style attribute
        content.getSpans(0, content.length, BackgroundColorSpan::class.java).forEach { span ->
            val start = content.getSpanStart(span)
            val end = content.getSpanEnd(span)
            val color = String.format("#%06X", 0xFFFFFF and span.backgroundColor)
            val htmlTag = "<span style=\"background-color:${color}\">${content.subSequence(start, end)}</span>"
            htmlContent = htmlContent.replaceRange(start, end, htmlTag)
        }

        return htmlContent
    }

}