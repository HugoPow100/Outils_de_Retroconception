#!/bin/bash
# Script pour compiler et exécuter Retroconception avec architecture MVC

# Se placer dans le dossier du script
cd "$(dirname "$0")"

# Compiler tous les fichiers Java avec les packages
echo "Compilation du projet..."
javac -d bin src/metier/*.java
if [ $? -ne 0 ]; then
    echo
    echo "✗ Erreur de compilation"
    exit 1
fi

echo
echo "✓ Compilation réussie"

# Vérifier si un argument est fourni
if [ $# -eq 0 ]; then
    echo "Usage: ./run.sh <fichier.java ou dossier>"
    exit 1
fi

# Exécuter le programme
java -cp bin metier.Retroconception "$@"
