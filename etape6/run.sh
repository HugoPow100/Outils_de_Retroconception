#!/bin/bash
# Script pour compiler et exécuter Retroconception avec architecture MVC

# Se placer dans le dossier du script
cd "$(dirname "$0")"

# Compiler tous les fichiers Java avec les packages
echo "Compilation du projet..."
javac -d class src/metier/lecture/*.java src/metier/objet/*.java src/metier/sauvegarde/*.java src/controleur/*.java src/vue/*.java src/metier/util/*.java src/vue/liaison/*.java src/vue/role_classe/*.java src/metier/util/test_structure_projet/*.java
if [ $? -ne 0 ]; then
    echo
    echo "✗ Erreur de compilation"
    exit 1
fi

echo
echo "✓ Compilation réussie"

# Exécuter le programme
java -cp class controleur.Controleur "$@"
