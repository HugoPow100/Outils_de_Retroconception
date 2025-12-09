#!/bin/bash
# Script pour compiler le projet avec architecture MVC

# Se placer dans le dossier racine du projet
cd "$(dirname "$0")"

# Compiler tous les fichiers Java avec les packages
echo "Compilation du projet..."
javac -d bin src/metier/*.java

if [ $? -eq 0 ]; then
    echo "✓ Compilation réussie"
else
    echo "✗ Erreur de compilation"
    exit 1
fi

#!/bin/bash
# Script pour exécuter Retroconception avec architecture MVC

# Se placer dans le dossier racine du projet
cd "$(dirname "$0")"

# Vérifier si un argument est fourni
if [ $# -eq 0 ]; then
    echo "Usage: ./run.sh <fichier.java ou dossier>"
    exit 1
fi

# Exécuter le programme
java controleur.Retroconception "$@"
