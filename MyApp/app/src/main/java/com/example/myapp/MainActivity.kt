package com.example.myapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapp.ui.theme.MyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel()

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { _: Boolean -> }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            MyAppTheme {
                AppPrincipal()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Canal de Notificações Diárias"
            val descriptionText = "Canal para notificações diárias do MyApp"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("daily_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

@Composable
fun AppPrincipal() {
    var abaSelecionada by remember { mutableStateOf(0) }

    val telas = listOf(
        "Notificações" to Icons.Default.Notifications,
        "Frases" to Icons.Default.Message,
        "Estatísticas" to Icons.Default.BarChart,
        "Configurações" to Icons.Default.LightMode
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF1E88E5)) {
                telas.forEachIndexed { index, (titulo, icone) ->
                    NavigationBarItem(
                        selected = abaSelecionada == index,
                        onClick = { abaSelecionada = index },
                        icon = { Icon(icone, contentDescription = titulo) },
                        label = {
                            Text(
                                text = titulo,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Yellow,
                            indicatorColor = Color(0xFF1565C0)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (abaSelecionada) {
                0 -> TelaNotificacao()
                1 -> TelaFrases()
                2 -> TelaEstatisticas()
                3 -> TelaConfiguracoes()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaNotificacao() {
    val context = LocalContext.current
    var textoNotificacao by remember { mutableStateOf("Clique abaixo para enviar sua notificação diária!") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificações Diárias", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E88E5))
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFFE3F2FD), Color(0xFFFFFFFF))))
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_dialog_info),
                    contentDescription = null,
                    tint = Color(0xFF1E88E5),
                    modifier = Modifier.size(72.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("Notificações Diárias", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E88E5))
                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().shadow(4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(textoNotificacao, textAlign = TextAlign.Center, fontSize = 16.sp, color = Color(0xFF333333))
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        enviarNotificacao(context)
                        textoNotificacao = "✅ Notificação enviada com sucesso!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(55.dp)
                ) {
                    Text("Enviar Notificação Agora", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun TelaFrases() {
    val frases = listOf(
        "Acredite em si mesmo e tudo será possível.",
        "Cada desafio é uma chance de crescer.",
        "Disciplina é o caminho para a liberdade.",
        "Você é mais forte do que imagina.",
        "Grandes coisas levam tempo."
    )
    var fraseAtual by remember { mutableStateOf(frases.random()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFE8F5E9), Color.White)))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "💬 Frase do Dia",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = fraseAtual,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp),
                    fontSize = 18.sp,
                    color = Color(0xFF1B5E20)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { fraseAtual = frases.random() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("Nova Frase", color = Color.White)
            }
        }
    }
}

@Composable
fun TelaEstatisticas() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFFFF9C4), Color.White)))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "📊 Estatísticas do Dia",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF57F17)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Progresso diário: 80%", fontSize = 18.sp)
            Text("Metas concluídas: 4/5", fontSize = 18.sp)
            Text("Nível de foco: Alto", fontSize = 18.sp)
        }
    }
}

@Composable
fun TelaConfiguracoes() {
    var temaEscuro by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFE3F2FD), Color.White)))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚙️ Configurações", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E88E5))
            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Tema Escuro", fontSize = 18.sp)
                Switch(checked = temaEscuro, onCheckedChange = { temaEscuro = it })
            }
        }
    }
}

fun enviarNotificacao(context: Context) {
    val builder = NotificationCompat.Builder(context, "daily_channel")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("MyApp - Notificação Diária")
        .setContentText("Lembre-se: cada dia é uma nova oportunidade de evoluir 💪")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notify(1, builder.build())
        }
    }
}
