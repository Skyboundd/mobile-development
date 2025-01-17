package com.skybound.helper

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class CareerPredictionHelper(context: Context, modelPath: String) {

    private val interpreter: Interpreter

    init {
        interpreter = Interpreter(loadModelFile(context, modelPath))
    }

    fun predictCareers(inputFeatures: FloatArray): List<String> {
        if (inputFeatures.size != 10) {
            throw IllegalArgumentException("Input features must be an array of 10 floats.")
        }

        val inputBuffer = ByteBuffer.allocateDirect(4 * inputFeatures.size).apply {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().put(inputFeatures)
        }

        val outputBuffer = Array(1) { FloatArray(104) }

        interpreter.run(inputBuffer, outputBuffer)

        val resultArray = outputBuffer[0]

        val daftarKarir = listOf(
            "Accountant", "Graphic Designer", "Salesperson", "Research Scientist", "Teacher",
            "Architect", "Nurse", "Software Developer", "Psychologist", "Chef", "Marketing Manager",
            "Physician", "Artist", "Human Resources Manager", "Construction Engineer", "Journalist",
            "Astronomer", "Financial Analyst", "Biologist", "Event Planner", "Real Estate Agent",
            "Environmental Scientist", "Lawyer", "IT Support Specialist", "Fashion Designer",
            "Marketing Coordinator", "Biomedical Engineer", "Event Photographer", "Data Analyst",
            "Pharmacist", "Social Worker", "Financial Planner", "Biotechnologist", "HR Recruiter",
            "Software Quality Assurance Tester", "Elementary School Teacher", "Industrial Engineer",
            "Market Research Analyst", "Financial Auditor", "Musician", "Police Detective",
            "Marketing Copywriter", "Zoologist", "Speech Therapist", "Mechanical Engineer",
            "Forensic Scientist", "Social Media Manager", "Geologist", "Web Developer",
            "Wildlife Biologist", "Air Traffic Controller", "Game Developer", "Urban Planner",
            "Financial Advisor", "Airline Pilot", "Environmental Engineer", "Interior Designer",
            "Physical Therapist", "Mechanical Designer", "Dental Hygienist", "Marketing Analyst",
            "Aerospace Engineer", "Pediatric Nurse", "Advertising Executive", "Wildlife Conservationist",
            "IT Project Manager", "Forestry Technician", "Video Game Tester", "Marriage Counselor",
            "Biomedical Researcher", "Database Administrator", "Public Relations Specialist",
            "Genetic Counselor", "Market Researcher", "Occupational Therapist", "Electrical Engineer",
            "Investment Banker", "Marine Biologist", "Human Rights Lawyer", "Database Analyst",
            "Pediatrician", "Technical Writer", "Forensic Psychologist", "Product Manager",
            "Fashion Stylist", "Speech Pathologist", "Public Health Analyst", "Sports Coach",
            "Insurance Underwriter", "Chiropractor", "Radiologic Technologist", "Tax Accountant",
            "Quality Control Inspector", "Rehabilitation Counselor", "Film Director", "Diplomat",
            "Police Officer", "Administrative Officer", "Tax Collector", "Foreign Service Officer",
            "Customs and Border Protection Officer", "Civil Engineer", "Robotics Engineer",
            "Electronics Design Engineer"
        )

        return resultArray
            .mapIndexed { index, probability -> index to probability }
            .sortedByDescending { it.second }
            .take(3)
            .map { daftarKarir[it.first] }
    }

    private fun loadModelFile(context: Context, modelPath: String): ByteBuffer {
        val assetFileDescriptor = context.assets.openFd(modelPath)
        val fileInputStream = assetFileDescriptor.createInputStream()
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(java.nio.channels.FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}

// Example usage

// Initialize the handler
//val predictionHandler = CareerPredictionHandler(this, "recommender-v2.tflite")

// Input features
//val inputFeatures = floatArrayOf(9.45f, 2.67f, 3.45f, 3.34f, 4.23f, 3.23f, 4.56f, 2.78f, 4.89f, 3.12f)

// Predict careers
//val topCareers = predictionHandler.predictCareers(inputFeatures)

// Display the result
//tvResult.text = "Top 3 Predicted Careers:\n${topCareers.joinToString(", ")}"

