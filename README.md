# Abalone Game

A Java implementation of the Abalone board game.

## Project Structure

```
src/
├── abalone/
│   ├── Main.java
│   ├── controller/
│   │   ├── GameController.java
│   │   └── GuiController.java
│   ├── model/
│   │   ├── Board.java
│   │   ├── Color.java
│   │   ├── Direction.java
│   │   ├── HexCoordinate.java
│   │   ├── Move.java
│   │   └── Piece.java
│   └── view/
│       ├── ConsoleView.java
│       ├── GamePanel.java
│       ├── GameWindow.java
│       └── HexLayout.java
```

## Compilation and Execution

### Linux / macOS

This project includes a **Makefile** for easy compilation and execution on Unix-like systems.

**Available commands:**

```bash
make compile    # Compile all Java source files
make run        # Compile (if needed) and run the game
make clean      # Remove all compiled files
make rebuild    # Clean and recompile
make help       # Display help message
```

**Example:**

```bash
make run
```

### Windows

This project includes a **batch file** (`build.bat`) for compilation and execution on Windows.

**Available commands:**

```cmd
build.bat compile    # Compile all Java source files
build.bat run        # Compile (if needed) and run the game
build.bat clean      # Remove all compiled files
build.bat rebuild    # Clean and recompile
build.bat help       # Display help message
```

**Note:** In PowerShell, you need to prefix the command with `.\`:

```powershell
.\build.bat run
```

In Command Prompt, you can run without the prefix:

```cmd
build.bat run
```

**Example:**

```cmd
build.bat run
```

## Requirements

- Java Development Kit (JDK) 8 or later
- `javac` and `java` commands must be available in your PATH

## Compilation Output

Compiled `.class` files are placed in the `bin/` directory.

To clean up compiled files, run:

- **Linux/macOS:** `make clean`
- **Windows:** `build.bat clean`
