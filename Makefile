# Makefile for Abalone Game

# Variables
JAVAC = javac
JAVA = java
SRCDIR = src
BINDIR = bin
MAIN_CLASS = abalone.Main

# Flags
JSON_JAR = $(SRCDIR)/abalone/model/java-json.jar
JAVAC_FLAGS = -cp "$(JSON_JAR)" -d $(BINDIR) -sourcepath $(SRCDIR)

# Find all Java source files
SOURCES = $(shell find $(SRCDIR) -name "*.java")
CLASSES = $(SOURCES:$(SRCDIR)/%.java=$(BINDIR)/%.class)

# Default target
.PHONY: all
all: compile

# Compile target
.PHONY: compile
compile: $(BINDIR) $(CLASSES)

$(BINDIR):
	mkdir -p $(BINDIR)

$(BINDIR)/%.class: $(SRCDIR)/%.java
	$(JAVAC) $(JAVAC_FLAGS) $<

# Run target
.PHONY: run
run: compile
	$(JAVA) -cp "$(JSON_JAR):$(BINDIR)" $(MAIN_CLASS)

# Clean target
.PHONY: clean
clean:
	rm -rf $(BINDIR)

# Help target
.PHONY: help
help:
	@echo "Available targets:"
	@echo "  make compile  - Compile the Java source files"
	@echo "  make run      - Compile and run the game"
	@echo "  make clean    - Remove compiled files"
	@echo "  make help     - Display this help message"

.PHONY: rebuild
rebuild: clean compile
