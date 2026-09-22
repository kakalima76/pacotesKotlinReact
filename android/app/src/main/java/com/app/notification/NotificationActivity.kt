package com.app.notification

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.app.R

class NotificationActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_notification)

        configurarJanela()

        val entrega = NovaEntregaDTO(
            estabelecimento = intent
                .getStringExtra("estabelecimento")
                .orEmpty(),

            quantidade = intent
                .getIntExtra("quantidade", 0),

            distancia = intent
                .getStringExtra("distancia")
                .orEmpty(),

            valor = intent
                .getStringExtra("valor")
                .orEmpty()
        )

        preencherTela(entrega)

        configurarBotoes()
    }

    private fun configurarJanela() {

        window.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )

        window.addFlags(
            WindowManager.LayoutParams.FLAG_DIM_BEHIND
        )

        val params = window.attributes

        params.width =
            (resources.displayMetrics.widthPixels * 0.90).toInt()

        params.height =
            WindowManager.LayoutParams.WRAP_CONTENT

        params.gravity = Gravity.CENTER

        params.dimAmount = 0.15f

        window.attributes = params
    }

    private fun preencherTela(entrega: NovaEntregaDTO) {

        findViewById<TextView>(
            R.id.establishmentName
        ).text = entrega.estabelecimento

        findViewById<TextView>(
            R.id.deliveryQuantity
        ).text = "${entrega.quantidade} entregas"

        findViewById<TextView>(
            R.id.distance
        ).text = entrega.distancia

        findViewById<TextView>(
            R.id.deliveryValue
        ).text = entrega.valor
    }

    private fun configurarBotoes() {

        findViewById<Button>(
            R.id.acceptButton
        ).setOnClickListener {

            finish()
        }

        findViewById<Button>(
            R.id.refuseButton
        ).setOnClickListener {

            finish()
        }
    }
}