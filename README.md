# 📌 Souls Adventure

Impugna il tuo arco e difenditi da schiere di mostri in questo RPG con
difficoltà selezionabile, progettato per offrire sfide sempre diverse e
calibrate sulle tue capacità. Ogni partita contribuisce al tuo storico
personale, tracciando progressi, punteggi e performance nel tempo.
---

## 🚀 Come eseguire il progetto

### Prerequisiti
- Java 25 (LTS)
- Gradle

### Istruzioni
```bash
   git clone [https://github.com/matteomigliore04/MPGC_RPG.git]
   cd MPGC_RPG
```

### Build del progetto
```bash
   ./gradlew build
```

### Esecuzione
```bash
   ./gradlew run
```
---

## 🤖 Uso di strumenti di AI

Durante lo sviluppo del progetto, sono stati utilizzati strumenti di AI come assistenti alla programmazione:

- Utilizzato Qwen / DeepSeek per:
  - Ottimizzazione delle classi, rimozione di duplicazioni e applicazione delle best practice.
  - Gestione delle collisioni e della fisica di spinta, risoluzione dei conflitti tra entità e configurazione delle dipendenze per i moduli JavaFX.
  - Stesura di commenti chiari e stringati per facilitare la manutenibilità del codice.


- Utilizzato Gemini per: generazione delle grafiche per i personaggi, i loot e per gli sfondi delle schemate, .
---

## Funzionalità Presenti

- Difficoltà: 4 livelli (Easy, Medium, Hard, Extreme) che scalano vita, velocità, spawn e ricompense.
- Gameplay: Movimento a 8 direzioni, sparo (con consumo frecce), pozioni e HUD completo.
- Nemici: 3 tipi (Goblin, Skeleton, Zombie) con IA d'inseguimento e anti-accavallamento.
- Collisioni: AABB per proiettili, Push-Out per spingere i nemici, Y-Sorting per la profondità visiva.
- Sistema ondate: Infinito, con scaling progressivo e bonus frecce.
- Statistiche: Salvataggio partite su CSV, schermata con Top 5 punteggi e dati generali.
- Audio: Musica per menu/gioco e suono dedicato al Game Over.
---

## Note Tecniche

- Architettura: Pattern MVC (Model-View-Controller).
- Game Loop: JavaFX AnimationTimer con calcolo del deltaTime per fluidità.
- Rendering: Canvas e GraphicsContext.
- Java 21: Uso massiccio di pattern matching, switch expressions e Math.clamp().
---

## TODO

### In Sviluppo:
- Caricamento livelli da file esterni.
- Abilità speciali.

### Pianificati:
- Schermata "Classifica Globale".
- Boss Fight ogni 5 ondate.
- Sistema di upgrade/inventario.

### Possibili Miglioramenti Futuri:
- Database SQLite al posto del CSV.
- Spritesheet animati.
- Sistema particellare per gli impatti.