#!/bin/bash
# Script pour compiler et exécuter Retroconception avec architecture MVC

# Se placer dans le dossier du script
cd "$(dirname "$0")"

# Compiler tous les fichiers Java avec les packages
echo "Compilation du projet..."
javac -d bin src/metier/*.java src/metier/lecture/*.java src/controlleur/*.java src/vue/*.java
if [ $? -ne 0 ]; then
    echo
    echo "✗ Erreur de compilation"
    exit 1
fi

echo
echo "✓ Compilation réussie"

# Exécuter le programme
java -cp bin vue.FenetrePrincipale "$@"
