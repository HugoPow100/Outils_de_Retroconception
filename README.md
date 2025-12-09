# Outil de Rétroconception Java-UML

## 🎯 Objectif
Développer un outil de rétro-conception permettant de générer des **diagrammes de classes UML** à partir de classes écrites en **Java**.  
Le projet doit être entièrement codé en Java en utilisant uniquement les bibliothèques du JDK.

## 📋 Contraintes
- Respect des règles d’écriture vues en cours.
- Code **commenté** et **documenté**.
- Deux modes d’utilisation :
  - **CUI** (Console User Interface)
  - **GUI** (Graphical User Interface)

## 🚀 Étapes du projet

### Étape 1 : Extraction des attributs et méthodes
- Entrée : fichier `.java` (chemin absolu).
- Sortie : liste des attributs et méthodes avec :
  - Nom
  - Type
  - Visibilité
  - Portée (instance ou classe)
  - Paramètres des méthodes

### Étape 2 : Affichage UML en mode texte
- Génération d’un diagramme UML simplifié en console.
- Exemple : `Point.java` et `Disque.java`.

### Étape 3 : Gestion de plusieurs classes
- Entrée : répertoire contenant plusieurs fichiers `.java`.
- Sortie : diagramme UML avec **associations** entre classes.
- Multiplicités gérées (0..*, 1..1, etc.).

### Étape 4 : Héritage et interfaces
- Détection des relations d’héritage.
- Gestion des interfaces (méthodes abstraites).
- Ajout des stéréotypes UML (`<<interface>>`).

### Étape 5 : Génération en mode GUI
- Chaque classe/interface représentée par un bloc déplaçable.
- Liens dynamiques entre blocs.
- Export possible en image.
- Sauvegarde des positions des classes.

### Étape 6 : Options avancées
- Modification des multiplicités par défaut.
- Ajout de rôles sur les associations.
- Ajout de propriétés prédéfinies (`{frozen}`, `{addOnly}`, `{requête}`).
- Sauvegarde dans un format lisible (ex. XML).

### Étape 7 : Fonctionnalités supplémentaires
- Gestion des méthodes par défaut dans les interfaces.
- Ajout de contraintes sur les associations ou généralisations.
- Gestion des classes internes.
- Support des classes abstraites.
- Limite d’évaluation : diagrammes de **10 classes max**.

## 📂 Organisation
- Un fichier `.java` = une seule classe.
- Tous les fichiers d’un projet dans un même répertoire.
- Les `import` en entête sont ignorés.
- Ordre : attributs puis méthodes.

## ✅ Résultats attendus
- Génération correcte de diagrammes UML (texte + GUI).
- Export en image.
- Outil flexible et extensible.

---
👨‍💻 Projet académique – IUT du Havre  
SAE 3.01 – Outil de rétroconception Java-UML
