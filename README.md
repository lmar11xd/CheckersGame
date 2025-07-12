# Juego de Damas

https://paulallies.medium.com/clean-architecture-in-the-flavour-of-jetpack-compose-dd4b0016f815

# Estructura
```
📁 core
└── app/MainActivity.kt
└── di/AppModule.kt
└── utils/Constants.kt

📁 domain
└── model/CheckersBoard.kt
└── model/Player.kt
└── repository/IGameRepository.kt
└── sound/ISoundPlayer.kt
└── usecase/

📁 data or infraestructure
└── repository/FirebaseGameRepository.kt
└── source/remote/GameRemoteDataSource.kt
└── sound/SoundPlayer.kt

📁 presentation
└── commons/components
└── commons/ui
└── ui/GameScreen.kt
└── ui/MainMenuScreen.kt
└── viewmodel/GameViewModel.kt

```

# Recomendaciones
Utilizar StateFlow (Moderno en Jetpack Compose) en lugar de LiveData para manejar el estado de la UI.

### Generador de Sonidos
https://sfxr.me/
https://www.bfxr.net/