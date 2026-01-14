### Compilation et exécution

#### Windows
./run.bat

#### Linux
./run.sh

### Compiler uniquement
javac -d class @compile.list

### Exécuter uniquement
java -cp class controleur.Controleur

# Présentation

- Ce projet est un Outil de rétroconception, il permet de génerer un Diagramme de classes   UML à partir d'un répertoire de classes Java.

- Attention : Lors du chargement d'un projet, les packages ne sont pas pris en compte, toutes les classes doivent être placées à la racine du répertoire selectionné.

# Besoin 
Ce projet a pour objectif de faciliter la compréhension et la prise en main rapide du code, en particulier lors de la reprise du projet par une personne qui n’en connaît pas le fonctionnement initial.
Il peut notamment être utilisé par des enseignants afin d’illustrer la transformation d’un projet Java en diagrammes UML, permettant ainsi une meilleure visualisation de la structure et de l’architecture du code.

# Utilisation

- Une fois le projet compilé et executé, une interface graphique s'ouvrira.

> Pour ajouter un projet, voir la barre du haut : Fichier -> Ouvrir un projet... et enfin ouvrir le répertoire contenant le projet Java votre choix.

> La barre latérale gauche affiche l'entièreté des projets sauvegardés. Cliquer sur un projet l'affichera sur le panneau principal, chargeant le diagramme.

### Sur le panneau principal, on peut :

- Déplacer les classes (clic gauche maintenu)
- Zoomer (ctrl + molette), et de se déplacer librement (clic droit maintenu)
- Ajouter des rôles aux associations (double clic gauche sur une classe -> Ajouter rôle)
- Modifier les rôles affichés (double clic gauche sur une classe -> Modifier rôle)
- Modifier les multiplicités affichées (double clic gauche sur une classe -> Modifier multiplicité)

### Fonctionnalités accessibles depuis la barre supérieure

- Ouvrir un nouveau projet Java  afficher dans la barre des projets (Fichier -> Ouvrir un projet...)
- Exporter en image ce qui est affiché sur le panneau de Diagramme (Fichier -> Exporter en image)
- Sauvegarder l'affichage actuel du schéma présent sur le panneau diagamme (Fichier -> Sauvegarder)
- Fermer l'interface (Fichier -> Quitter)
- Afficher ou non les attributs des classes (Affichage -> Afficher attributs)
- Afficher ou non les méthodes des classes (Affichage -> Afficher méthodes)
- Optimiser les positions des blocs et liaison (Affichage -> Optimiser les positions)
- Optimiser les liaisons seulement (Affichage -> Optimiser les liaisons uniquement)
- Avoir les détails des auteurs (Aide -> A propos)

La liste des projets sauvegardés et leur position est sauvegardée dans le fichier data/projets.xml sous la forme : «chemin absolu» (tabulation) «chemin avec le ficher de sauvegarde du projet en question»
Les diagrammes sont sauvegardés dans data/sauvegarde/ avec toutes les informations relatives aux classes et leurs liaisons.
Les données des diagrammes peuvent être changées directement sur les fichiers .xml dans data, via un éditeur de texte.

### Fonctionnalités de la section de projets
- Choisir le projet a afficher dans le panneau diagramme (clique gauche sur le projet voulu)
- Renommer un projet (clique droit sur le projet voulu -> Renommer projet)
- Supprimer un projet (clique droit sur le projet voulu -> Supprimer projet)

Un projet présent dans `projets.xml` mais inexistant sur l'ordinateur est ignoré dans la liste des projet lors du lancement.

## Structure

Le projet est organisé en modèle MVC : Métier-Vue-Controleur.
En MVC, le métier gère l'écriture, sauvegarde, le traitement de données. La vue gère l'affichage et l'interface Homme-Machine. Le controleur gère la liaison entre la vue et le métier.


C:.  
│&nbsp;&nbsp;&nbsp;compile.list  
│&nbsp;&nbsp;&nbsp;Documentation.md  
│&nbsp;&nbsp;&nbsp;Executer.md  
│&nbsp;&nbsp;&nbsp;run.bat  
│&nbsp;&nbsp;&nbsp;run.sh  
│  
├───class  
│  
├───data  
│&nbsp;&nbsp;&nbsp;│  
│&nbsp;&nbsp;&nbsp;├───donnees  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;projets.xml  
│&nbsp;&nbsp;&nbsp;│  
│&nbsp;&nbsp;&nbsp;└───sauvegardes  
│  
├───src  
│&nbsp;&nbsp;&nbsp;│  
│&nbsp;&nbsp;&nbsp;├───controleur  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Controleur.java  
│&nbsp;&nbsp;&nbsp;│  
│&nbsp;&nbsp;&nbsp;├───metier  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;├───lecture  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;AnalyseurFichier.java  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;GenerateurAssociation.java  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Lecture.java  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ParseurJava.java  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;UtilitaireType.java  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;├───objet  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Association.java  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Attribut.java  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Classe.java  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Heritage.java  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Interface.java  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Liaison.java  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Methode.java  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Multiplicite.java  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Parametre.java  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;├───sauvegarde  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;GestionSauvegarde.java  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;└───util  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;ConstantesChemins.java  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└───test_structure_projet  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ElementStructureProjet.java  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;TypeElement.java  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;VerificationStructureProjet.java  
│&nbsp;&nbsp;&nbsp;│  
│&nbsp;&nbsp;&nbsp;├───res  
│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;uml_icon.png  
│&nbsp;&nbsp;&nbsp;│  
│&nbsp;&nbsp;&nbsp;└───vue  
│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;BarreMenus.java  
│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;BlocClasse.java  
│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;FenetrePrincipale.java  
│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;PanneauDiagramme.java  
│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;PanneauProjets.java  
│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│  
│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;├───liaison  
│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;CalculateurChemin.java  
│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DetecteurObstacles.java  
│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;GestionnaireAncrage.java  
│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;GestionnaireIntersections.java  
│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;LiaisonVue.java  
│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;RenduLiaison.java  
│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│  
│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└───role_classe  
│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;FenetreChangementMultiplicite.java  
│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;FenetreModifRole.java  
│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;PanneauModif.java  
│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;PanneauModifRole.java  

## Description du système

Cette section décrit le fonctionnement du système d'ouverture/analyse/sauvegarde/chargement de l'aplication.
<br>Pour une description détaillée de la structure et des classes, voir la section "Description détaillée des classes"

### Chargement d'un projet depuis une liste de .java

Lorsque l'utilisateur charge une première fois un projet, le programme va récuperer le chemin absolu de celui ci et l'envoyer au métier (en passant par le controlleur), plus précisément la classe "Lecture".
Lecture et ses classes associées (AnalyseurFicher, GenerateurAssociation, ParseurJava et UtilitaireType) vont instancier différentes listes de Classes, Interfaces, Heritages et Associations.

La lecture se fait via un scanner qui analyse ligne par ligne les classes Java pour en extraire des objets tels que :
- **Classe**   : A un `nom`, un  `type` (*class*/*abstract*). Elle stocke si elle a un héritage avec `isHeritage`, une interface dans `nomInterface`. Elle stocke une liste d'`attributs` et de `méthodes`.
- **Attribut** : A un `nom`, un `type` , une `visibilité` (*private*/*public*/*protected*/*package*), une `portee` (*instance*/*classe). et `isConstant`
- **Methode**  : A un `nom`, une type de `retour`, une `visibilite` (*private*/*public*/*protected*/*package*), un booléen `isAbstract`, et une liste de **Parametre** (un paramètre à un `nom` et un `type`).

- **Liaison** : A une classe d'origine (`classeOrig`) et de destination (`classeDest`).
Différents types de "liaisons" entre 2 classes :
    - **Interface**   : Hérite de **Liaison**.
    - **Heritage**    : Hérite de **Liaison**.

    Si le type d'un attribut correspond au nom d'une autre Classe du projet, il est supprimé et changé en **Association** : 
    - **Association** : Hérite de **Liaison**, stocke un booléen si il est `unidirectionnel`, une multiplicité d'origine (`multiOrig`) et de destination (`multiDest`).

- **Multiplicite** : Contient deux int `debut` et `fin`. Exemple : (0..1) et si (0..*), alors "fin" est égal a Integer.MAX_INT (interpreté comme *)


Une fois toute la lecture terminée, les informations sont transférées via le Controleur à la vue (**FenetrePrincipale**), convertit les **Classe** en **BlocClasse** et les **Liaison** en **LiaisonVue**, qui seront utilisées dans la vue :

- **BlocClasse** : A tout les attribut de **Classe**, mais aussi un booléen `estInterface`, les int `x` `y` de sa position et `largeur` et `hauteur`.
- **LiaisonVue** : A tout les atrtibuts de **Association** (et donc de tout les autres types de liaison), mais aussi deux **Point**s `ancrageOrigine` et `ancrageDestination`.

Ces instances de classes sont ensuite envoyées à la vue par le controleur, et sont affichées sur le Diagramme de classe par la vue.

### Sauvegarde d'un projet

Lorsque l'utilisateur sauvegarde son diagramme, son contenu sera enregistré dans `data/sauvegardes/«nom du projet».xml`. Voir la classe `GestionSauvegarde`, dans les méthodes `sauvegarderClasses`, `sauvegarderCoordProjet`, `sauvegarderLiaison`.

> Il est important de notifier que le format de fichier XML ne correspond pas au format du contenu, et que ces fichiers auraient du être signés en .data à la place.

**Le contenu du diagramme sera sauvegardé sous format texte dans cette forme :**

---

`<lien du fichier>`

*\---- Classes \----*

`nomBlocClasse`	`abcisse`    `ordonnee`	`largeur`	`hauteur`	`estInterface`
>Les données de classes sont traitées lors de la sauvegarde/chargement. Il peut y avoir autant de classes que possible dans un diagramme. Une classe peut contenir une liste d'attributs et méthodes.
> Exemple :
> ```
> Animal    50    50    200    150    false

`-/+/~` `nomAttribut` : `type` `{frozen/addOnly/requête}`

`-/+/~` `nomMethode`(`nomParam` : `typeParam`, ...) : `type` 

>Les attributs et méthodes sont traités comme des chaînes de caractère. Il peut y avoir autant d'attributs et méthodes dans une classe que possible.
> Exemple :
> ```
> - nom : String
> - age : int
> + Animal(nom : String, age : int)
> + getNom() : String

*\---- Liaisons \----*

`typeLiaison`	`id`	`blocOrig`	`coteOrig`	`posRelOrig`	`blocDest`	`coteDest`	`posRelDest`	`roleOrig`	`roleDest`	`multiOrig`	`multiDest`

>Les données de liaisons sont traitées lors de la sauvegarde/chargement. Il peut y avoir autant de liaisons que possible dans un diagramme. Une classe peut contenir une liste de classes et méthodes.
>`typeLiaison` inclut `heritage`/`association_uni`/`association_bi`/`interface`
>`blocOrig` et `blocDest` correspondent aux noms des classes d'origine et de destination de la liaison.
> Exemple : 
> ```
>heritage    0    Chat    TOP    0.5    Animal    BOTTOM    0.5    ?    ?    ?    ?
>association_uni    1    Chat    RIGHT    0.5    Collier    LEFT    0.5    ?    monCollier    1..1    0..*


---


Toutes les informations doivent être séparées par des tabulations, sous les balise "\---- Classes \----" ou "\---- Liaisons \----".
Les lignes commencant par ``#`` ne sont pas prises en compte par le lecteur.


### Chargement d'un projet en format XML

Lorsque l'utilisateur sélectionne un projet dans la barre de gauche, si le projet est déjà existant dans `data/sauvegardes/«nom du projet».xml`, le chargement se fera depuis ce XML plutot que sur le projet de classes .java.
La lecture par l'application se basera sur les mêmes critères que la sauvegarde (ci-dessus). Voir la classe `GestionSauvegarde`, dans la méthode `sauvegardeProjetXml`.
Ainsi, les classes seront chargées d'après le principe du premier chargement (**BlocClasse**, **LiaisonVue**...).

<br><br>

# Description détaillée des classes

Voici une description détaillée de toutes les classes et leurs attributs & méthodes.

## <u>Partie Controleur</u>

Dans le modèle MVC, le controlleur fait le lien entre la vue et le métier. <br> 


### Présentation
Le **Controleur** est le point central de l'application, assurant la communication entre le **métier** (lecture et analyse des classes Java) et la **vue** (affichage graphique des diagrammes UML). Il gère le chargement des projets, la création des blocs de classes (`BlocClasse`) et des liaisons (`LiaisonVue`), ainsi que <u>la sauvegarde et la récupération des projets.</u>

---

### Structure


#### Attributs

| Attribut               | Type                     | Description                                                                 |
|------------------------|--------------------------|-----------------------------------------------------------------------------|
| `lecture`              | `Lecture`                | Instance pour analyser les fichiers Java et extraire les classes, attributs, méthodes, héritages, interfaces et associations. |
| `fenetrePrincipale`    | `FenetrePrincipale`      | Référence à la fenêtre principale de l’IHM pour afficher les blocs et liaisons. |
| `gestionSauvegarde`    | `GestionSauvegarde`      | Instance pour gérer les sauvegardes des projets en XML.                  |
| `lstLiaisons`          | `List<LiaisonVue>`       | Liste des liaisons entre les blocs (associations, héritages, interfaces). |
| `lstBlocs`             | `List<BlocClasse>`       | Liste des blocs représentant les classes et interfaces dans le diagramme. |

---

### <div style="color:#E74C3C;">Constructeur</div>

#### <div style="color:#E74C3C;"> Controleur()</div>

Initialise le contrôleur, les listes de blocs et de liaisons, ainsi que les instances de `GestionSauvegarde` et `FenetrePrincipale`.


- <strong>Rôle</strong> : Initialise le contrôleur, les listes de blocs et de liaisons, ainsi que les instances de `GestionSauvegarde` et `FenetrePrincipale`.
- <strong>Détails</strong> :
  - `lstLiaisons` et `lstBlocs` sont initialisées comme des listes vides.
  - `gestionSauvegarde` et `fenetrePrincipale` sont initialisées avec `this` pour permettre la communication entre les composants.

---

#### <u>Méthodes</u>

#### <div style="color:#FFB347;"> static void main(String[] args)</div>

<div style="color:#5D8AA8;">
Point d’entrée de l’application.

- <strong>Rôle</strong> : Point d’entrée de l’application.
- <strong>Détails</strong> :
  - Vérifie la structure du projet avec `VerificationStructureProjet`.
  - Crée une instance de `Controleur` et affiche la `FenetrePrincipale`.
</div>

---

#### <div style="color:#FFB347;">void chargerProjet(String cheminProjet)</div>

<div style="color:#5D8AA8;">
Charge un projet à partir d’un chemin donné.


- <strong>Rôle</strong> : Charge un projet à partir d’un chemin donné.
- <strong>Paramètres</strong> :
  - `cheminProjet` : Chemin absolu du répertoire contenant les classes Java.
- <strong>Détails</strong> :
  - Vérifie si le projet est déjà sauvegardé dans un fichier XML.
  - Si le projet est sauvegardé, il est chargé depuis le fichier XML (`chargerProjetDepuisXml`).
  - Sinon, il est chargé depuis les fichiers Java (`chargerProjetDepuisJava`).
</div>

---

#### <div style="color:#FFB347;">void chargerProjetDepuisXml(String intituleProjet)</div>

<div style="color:#5D8AA8;">
Charge un projet à partir d’un fichier XML de sauvegarde.


- <strong>Rôle</strong> : Charge un projet à partir d’un fichier XML de sauvegarde.
- <strong>Paramètres</strong> :
  - `intituleProjet` : Nom du projet (utilisé pour trouver le fichier XML correspondant).
- <strong>Détails</strong> :
  - Récupère le chemin du projet à partir de l’intitulé.
  - Charge les blocs de classes et les liaisons depuis le fichier XML.
  - Met à jour `lstBlocs` et `lstLiaisons`.
</div>

---

#### <div style="color:#FFB347;">void chargerProjetDepuisJava(String cheminProjet)</div>

<div style="color:#5D8AA8;">
Charge un projet à partir des fichiers Java.


- <strong>Rôle</strong> : Charge un projet à partir des fichiers Java.
- <strong>Paramètres</strong> :
  - `cheminProjet` : Chemin absolu du répertoire contenant les classes Java.
- <strong>Détails</strong> :
  - Crée une instance de `Lecture` pour analyser les fichiers Java.
  - Convertit les classes Java en `BlocClasse` et les ajoute à `lstBlocs`.
  - Crée les liaisons (associations, héritages, interfaces) et les ajoute à `lstLiaisons`.
  - Optimise les positions des blocs et des liaisons dans la vue.
</div>

---

#### <div style="color:#FFB347;">BlocClasse créerBlocAPartirDeClasse(Classe classe, int x, int y)</div>

<div style="color:#5D8AA8;">
Crée un `BlocClasse` à partir d’une instance de `Classe`.


- <strong>Rôle</strong> : Crée un `BlocClasse` à partir d’une instance de `Classe`.
- <strong>Paramètres</strong> :
  - `classe` : Instance de `Classe` à convertir.
  - `x` : Coordonnée X du bloc.
  - `y` : Coordonnée Y du bloc.
- <strong>Retourne</strong> : Un `BlocClasse` initialisé avec les attributs et méthodes de la classe.
- <strong>Détails</strong> :
  - Convertit les attributs et méthodes de la classe en chaînes de caractères formatées.
  - Définit si le bloc représente une interface.
</div>

---

#### <div style="color:#FFB347;"> List&lt;LiaisonVue&gt; créerLiaisonsDepuisAssoc(List&lt;Association&gt; lstAssoc, HashMap&lt;String, BlocClasse&gt; mapBlocsParNom, List&lt;LiaisonVue&gt; lstLiaisons)</div>

<div style="color:#5D8AA8;">
Crée des liaisons de type "association" à partir d’une liste d’associations.

- <strong>Rôle</strong> : Crée des liaisons de type "association" à partir d’une liste d’associations.
- <strong>Paramètres</strong> :
  - `lstAssoc` : Liste des associations.
  - `mapBlocsParNom` : HashMap associant les noms de classes aux blocs.
  - `lstLiaisons` : Liste des liaisons existantes.
- <strong>Retourne</strong> : La liste des liaisons mise à jour.
- <strong>Détails</strong> :
  - Pour chaque association, crée une `LiaisonVue` entre les blocs correspondants.
</div>

---

#### <div style="color:#FFB347;"> List&lt;LiaisonVue&gt; créerLiaisonsDepuisHerit(List&lt;Heritage&gt; lstHerit, HashMap&lt;String, BlocClasse&gt; mapBlocsParNom, List&lt;LiaisonVue&gt; lstLiaisons)</div>

<div style="color:#5D8AA8;">
Crée des liaisons de type "héritage" à partir d’une liste d’héritages.

- <strong>Rôle</strong> : Crée des liaisons de type "héritage" à partir d’une liste d’héritages.
- <strong>Paramètres</strong> :
  - `lstHerit` : Liste des héritages.
  - `mapBlocsParNom` : HashMap associant les noms de classes aux blocs.
  - `lstLiaisons` : Liste des liaisons existantes.
- <strong>Retourne</strong> : La liste des liaisons mise à jour.
- <strong>Détails</strong> :
  - Pour chaque héritage, crée une `LiaisonVue` entre les blocs correspondants.
</div>


---

#### <div style="color:#FFB347;">List&lt;LiaisonVue&gt; créerLiaisonsDepuisInterface(List&lt;Interface&gt; lstInter, HashMap&lt;String, BlocClasse&gt; mapBlocsParNom, List&lt;LiaisonVue&gt; lstLiaisons)</div>

<div style="color:#5D8AA8;">
Crée des liaisons de type "interface" à partir d’une liste d’interfaces.

- <strong>Rôle</strong> : Crée des liaisons de type "interface" à partir d’une liste d’interfaces.
- <strong>Paramètres</strong> :
  - `lstInter` : Liste des interfaces.
  - `mapBlocsParNom` : HashMap associant les noms de classes aux blocs.
  - `lstLiaisons` : Liste des liaisons existantes.
- <strong>Retourne</strong> : La liste des liaisons mise à jour.
- <strong>Détails</strong> :
  - Pour chaque interface, crée une `LiaisonVue` entre les blocs correspondants.
</div>

---

#### <div style="color:#FFB347;">void sauvegardeProjetXml(String cheminFichier)</div>

<div style="color:#5D8AA8;">
Sauvegarde le projet dans un fichier XML.

- <strong>Rôle</strong> : Sauvegarde le projet dans un fichier XML.
- <strong>Paramètres</strong> :
  - `cheminFichier` : Chemin du fichier à sauvegarder.
- <strong>Détails</strong> :
  - Délègue la sauvegarde à `gestionSauvegarde`.
</div>

---

#### <div style="color:#FFB347;">void sauvegarderClasses(List&lt;BlocClasse&gt; listBlocClasses, List&lt;LiaisonVue&gt; listLiaison, String cheminProjet)</div>

<div style="color:#5D8AA8;">
Sauvegarde les blocs et les liaisons dans un fichier
- <strong>Rôle</strong> : Sauvegarde les blocs et les liaisons dans un fichier.
- <strong>Paramètres</strong> :
  - `listBlocClasses` : Liste des blocs à sauvegarder.
  - `listLiaison` : Liste des liaisons à sauvegarder.
  - `cheminProjet` : Chemin du projet.
- <strong>Détails</strong> :
  - Délègue la sauvegarde à `gestionSauvegarde`.
</div>

---

#### <div style="color:#FFB347;">void ajouterBlockList(BlocClasse block)</div>

<div style="color:#5D8AA8;">
Ajoute un bloc à la liste des blocs

- <strong>Rôle</strong> : Ajoute un bloc à la liste des blocs.
- <strong>Paramètres</strong> :
  - `block` : Bloc à ajouter.
</div>

<br>

# <u>Partie Métier</u>


Dans le modèle MVC, le métier gère la logique des entités, des traitements de données et des lectures.<br>
Métier comporte 4 packages.
- **objet** : Contient toutes les entités qui représentent les élements de classe Java.<br>
- **lecture** : Gère l'analyse de classes Java pour les convertir en objets.<br>
- **util** : Contient des outils et constantes utilisées dans tout le projet.<br>
- **sauvegarde** : Contient la gestion de sauvegarde et chargement en format XML.


<br><br>

## <u>Package metier.objet</u>

### <div style="color:#E74C3C;">Association</div>

Cette classe permet de créer des objets d'Associations.<br>
Hérite de Liaison.

- <strong>Attributs</strong> : nbAssoc(identifiant unique), multiOrig, multiDest, num  
- <strong>Méthodes</strong> : Getters, Setters, isUnidirectionnel(savoir si la classe est Unidirectionnel) et un toString.

---

### <div style="color:#E74C3C;">Attribut</div>

Cette classe permet de créer des objets d'Attributs.<br>
Elle contient des caractéristiques propres à un attribut.

- <strong>Attributs</strong> : nom, type, visibilite, portee (instance ou classe)  
- <strong>Méthodes</strong> : Getters et Setters, ainsi qu'un toString propre.

---

### <div style="color:#E74C3C;">Methode</div>

Cette classe permet de créer des objets de méthodes.<br>
Elle contient des caractéristiques propres à une méthode.

- <strong>Attributs</strong> : nom, retour(type de retour), visibilite, isAbstract, lstParametre  
- <strong>Méthodes</strong> : Getters, Setters, un toString et une méthode pour ajouter un paramètre à la liste

---

### <div style="color:#E74C3C;">Parametre</div>

Cette classe permet de créer des objets de parametres.<br>
Elle contient des caractéristiques propres à un parametre.

- <strong>Attributs</strong> : nom, type  
- <strong>Méthodes</strong> : Getters, Setters et un toString

---

### <div style="color:#E74C3C;">Classe</div>

Cette classe permet de créer des objets de Classe.<br>
Elle contient des caractéristiques propres à une classe.

- <strong>Attributs</strong> : nom, classeParente(nom de la classe hérité), type, isHeritage, nomInterface, liste d'attributs et de méthode  
- <strong>Méthodes</strong> : Getters et des méthodes booléennes qui nous serviront dans les fichiers du dossiers lecture pour détecter de quel type de classe il s'agit.

---

### <div style="color:#E74C3C;">Liaison</div>

Cette classe permet de créer un objet Liaison qui représente les relations entre deux classes comme par exemple l'héritage ou les interfaces.

- <strong>Attributs</strong> : classeOrigine(classe source), classeSource(classe cible)  
- <strong>Méthodes</strong> : Getters

---

### <div style="color:#E74C3C;">Heritage</div>

Cette classe permet de créer un objet Heritage afin de déterminer quel classe est à l'origine de l'héritage et quel classe hérite de la classe d'origine.<br>
Hérite de la classe Liaison.

- <strong>Attributs</strong> : herite des attributs de Liaison  
- <strong>Méthode</strong> : toString

---

### <div style="color:#E74C3C;">Interface</div>

Cette classe permet de créer un objet Interface afin de déterminer quelle classe est l'interface implémentée et quelle classe implémente l'interface.<br>
Hérite de la classe Liaison.

- <strong>Attributs</strong> : herite des attributs de Liaison  
- <strong>Méthode</strong> : toString

---

### <div style="color:#E74C3C;">Multiplicite</div>

Cette classe permet de créer des objets de multiplicite, qui représente la multiplicité d'une association ou d'une relation entre deux classes.

- <strong>Attributs</strong> : debuts, fin  
- <strong>Méthodes</strong> : Deux constructeurs, setters et un toString.

Constructeur 1 : Verifie que les valeurs des multiplicités sont correctes.  
Constructeur 2 : Verifie si la valeur est un '*' auquel cas on lui affecte ce caractere.


<br>

## <u>Package metier.lecture</u>

### <div style="color:#E74C3C;">Classe ParseurJava</div>

Cette classe permet d'analyser un fichier .java, et de créer une classe.
Elle analyse bien sur les attributs, les méthodes etc. C'est ce qui va créer la classe.

Cette classe est appelée dans la classe AnalyserFichier.

#### <div style="color:#FFB347;">ParseurJava (constructeur)</div>

<div style="color:#5D8AA8;">
Le constructeur initialise les attributs d'instance.
</div>

---

#### <div style="color:#FFB347;">Classe parser(Scanner scFic, String nomFichierAvExt)</div>

<div style="color:#5D8AA8;">
C'est la methode principale de cette classe qui permet d'analyser le fichier, de créer la classe et de la retourner.


 Elle fait appel à des sous-méthodes pour gérer les cas des attributs, des méthodes, des records etc.
</div>

---

#### <div style="color:#FFB347;">Attribut parserAttribut(String ligne)</div>

<div style="color:#5D8AA8;">
Cette méthode permet de creer et de retourner un Attribut, qu'elle ajoute par la suite dans la liste d'attributs.<br>
Elle extrait la visibilite, les modificateurs, la portée(classe/Instance), le type et le nom.
</div>

---

#### <div style="color:#FFB347;">Methode parserMethode(String ligne, String nomFichier, String typeClasse, boolean ligneCommenceParModificateur, boolean estMethodeInterface, String nomMethode, String nomConstructeur, List&lt;Parametre&gt; lstParametres)</div>

<div style="color:#5D8AA8;">
Cette méthode permet de creer et de retourner une Methode.<br>
Elle extrait la visibilite, les modificateurs(si abstract est présent), la portée(classe/Instance), le type et les parametres.<br>
Pour les parametres nous appelons la méthode parserParametre.<br>
Et enfin nous verifions bien si la méthode en question est un constructeur en fonction de la nom de la methode.
</div>

---

#### <div style="color:#FFB347;">Parametre parserParametre(String params)</div>

<div style="color:#5D8AA8;">
Cette méthode analyse la chaine qui contient les parametres entre parenthèses.<br>
Ensuite elle creer et retourne une liste de parametre avec les éléments de la chaine. Elle extrait donc le nom et le type du parametre puis l'ajoute a la liste.

<br>

> On vérifie bien si le type du parametres est un type comme des arraylist ou des hashmap, pour pouvoir découper la chaine.

</div>


---

### <div style="color:#E74C3C;">GenerateurAssociation</div>
Classe responsable de la génération des associations entre classes.

#### <div style="color:#FFB347;">ArrayList<Association> generer()</div>

<div style="color:#5D8AA8;">
Permet de gérer les associations à faire avec les classes, en prenant en compte les attributs à retirer du bloc grâce à une liste d'attruts. Elle retourne une Liste d'association
</div>
    
---

#### <div style="color:#FFB347;">void traiterMultiInstance(Classe classeOrig, ArrayList&lt;String&gt; listeMultiInstance)</div>

<div style="color:#5D8AA8;">
Méthode qui traite les associations multi-instance (tableaux, List, Set, Map). Trouve automatiquement la classe de destination.</div>
    
---

#### <div style="color:#FFB347;">void traiterSimpleInstance(Classe classeOrig, ArrayList&lt;String&gt; listeMultiInstance)</div>
<div style="color:#5D8AA8;">Méthode qui traite les associations à instance unique. Trouve automatiquement la classe de destination.</div>

---

#### <div style="color:#FFB347;">boolean estBidirectionnel(Classe classeOrig, Classe classeDest)</div>
<div style="color:#5D8AA8;">Méthode qui vérifie si une association donnée est bidirectionnelle. Renvoie un booléen.</div>
    
---

#### <div style="color:#FFB347;">void nettoyerAssociations()</div>
<div style="color:#5D8AA8;">Méthode qui nettoie sa liste des associations pour supprimer les doublons et transformer les doublons en associations bidirectionnelles.</div>

---

### <div style="color:#E74C3C;">AnalyseurFichier</div>
Classe qui parcourt les classes java données, puis qui délègue l'analyse syntaxique à la classe ParseurJava.
#### <div style="color:#FFB347;">HashMap<String, Classe> analyser(String cheminFichier)</div>

<div style="color:#5D8AA8;">
Permet de définir si le document en question choisi par l'utilisateur<br>
- Si c'est un répertoire, on creer une liste de fichiers, qui seront ensuite découpé.<br>
- Si c'est un fichier, on utilise la classe ParseurJava afin de découper le fichier.java
</div>

---

### <div style="color:#E74C3C;">UtilitaireType</div>

Classe utilitaire pour manipuler les types Java.


#### <div style="color:#FFB347;">void nettoyerType(String type)</div>

<div style="color:#5D8AA8;">
Nettoie le string de type entré pour retirer des élements en trop comme "List&lt;&gt;", "Set&lt;&gt;" ou "[]" et renvoie le résultat
</div>

---
    
#### <div style="color:#FFB347;">boolean estMultiInstance(String type)</div>

<div style="color:#5D8AA8;">
Retourne true si le type peut contenir plusieurs instances (tableau ou collection).
</div>

<br><br>

    
## <u>Package metier.sauvegarde</u>

### <div style="color:#E74C3C;">GestionSauvegarde</div>

#### <div style="color:#FFB347;">List<LiaisonVue> lectureLiaison(String dossierFichSelec, Map&lt;String, BlocClasse&gt; hashMapBlocClass)</div>

<div style="color:#5D8AA8;">
Permet de parcourir le fichier de la <strong>fenêtre principale de l’application UML</strong>.<br>
Elle structure l’interface graphique en regroupant les panneaux projets et diagramme.<br>
Elle assure le <strong>rôle de point central entre l’IHM et le contrôleur</strong>.<br>
Permet de lire les liaisons UML (associations, héritages, etc.) depuis un fichier de sauvegarde du projet, puis recréer les objets LiaisonVue correspondants dans l’application.
</div>

---

#### <div style="color:#FFB347;">Map<String, BlocClasse> chargerBlocsClasses(String nomProjet)</div>

<div style="color:#5D8AA8;">
La méthode renvoie <code>Map&lt;String, BlocClasse&gt;</code>, permettant la création de tous les blocs à créer du fichier Java mis en paramètres.<br>
Chaque bloc créer est alors mis dans une HashMap avec le String : Nom du bloc, puis BlocClasse : Le bloc initialisé à ajouter.
</div>

---

#### <div style="color:#FFB347;">void sauvegarderClasses(List&lt;BlocClasse&gt; listBlocClasses, List&lt;LiaisonVue&gt; listLiaison, String cheminProjet)</div>
<div style="color:#5D8AA8;">
Sauvegarder l’état complet d’un projet UML

- les classes UML (positions, dimensions, attributs, méthodes)

- les liaisons entre les classes

- référencer le projet dans le fichier projets.xml s’il n’existe pas encore
</div>

---

#### <div style="color:#FFB347;"> void sauvegarderCoordProjet(List<BlocClasse> listBlocClasses, String nomProjet, String cheminProjet) </div>
    
<div style="color:#5D8AA8;">
Permet d'écrire les coordonées des classes dans le .xml du fichier lier au .java.
</div>
    
---
#### <div style="color:#FFB347;"> void sauvegardeProjetXml </div>
<div style="color:#5D8AA8;">
Permet de sauvegarder le projet en XML, afin de pouvoir réutiliser les valeurs ( Position, Nom, Classes, Méthodes, Attributs...) lorsque l'on réouvrira le fichier après l'avoir sauvegarder
</div>

<br><br>

## <u>Partie Vue</u>


Dans le modèle MVC, la vue est le contenu de l'interface homme-machine.
La vue comporte 2 packages et des classes sans package.


<br>

- <strong>liaison</strong> : Gère le calcul et l'affichage des liaisons<br>
- <strong>role_classe</strong> : Gère les élements de modification/d'ajout de rôles et de classes

<br>

### <div style="color:#E74C3C;">FenetrePrincipale</div>
---

#### <div style="color:#FFB347;">FenetrePrincipale(...) (constructeur)</div>
<div style="color:#5D8AA8;">
Initialise la fenêtre, crée les panneaux principaux, configure le layout et installe la barre de menus.
</div>

---

#### <div style="color:#FFB347;">ouvrirProjet(...)</div>
<div style="color:#5D8AA8;">
Charge un projet UML dans le panneau diagramme, déclenche la sauvegarde initiale via le contrôleur et force le recalcul graphique des liaisons.
</div>

---

#### <div style="color:#FFB347;">exporterImageDiagramme(...)</div>
<div style="color:#5D8AA8;">
Capture le diagramme affiché dans le panneau diagramme et l’exporte au format PNG en gérant temporairement le zoom et l’affichage du texte.
</div>

---

#### <div style="color:#FFB347;">Méthodes secondaires</div>
<div style="color:#5D8AA8;">
- <strong>affichageAttributs(...)</strong> : appelle <code>panneauDiagramme.setAfficherAttributs(...)</code> pour
panneauDiagramme.optimiserPositionsLiaisons()</code> pour recalculer la position des liaisons.<br>
- <strong>setSauvegardeAuto(...)</strong> : appelle <code>panneauDiagramme.setSauvegardeAuto(...)</code> pour activer ou désactiver la sauvegarde automatique.<br>
- <strong>actionSauvegarder()</strong> : appelle <code>panneauDiagramme.actionSauvegarder()</code> pour sauvegarder l’état courant du diagramme.<br>
- <strong>chargerProjet(...)</strong> : délègue l’appel à <code>controleur.chargerProjet(...)</code>.<br>
- <strong>sauvegarderClasses(...)</strong> : délègue la sauvegarde des classes et liaisons à <code>controleur.sauvegarderClasses(...)</code>.<br>
- <strong>viderDiagramme()</strong> : appelle <code>panneauDiagramme.viderDiagramme()</code> pour réinitialiser l’affichage.
</div>

<br><br>


### <div style="color:#E74C3C;">PanneauProjets</div>

Cette classe représente le <strong>panneau latéral de gestion des projets</strong> de l’application.
Elle affiche la liste des projets enregistrés, permet leur sélection et leur gestion (renommer, supprimer).
Elle agit comme une <strong>vue dédiée</strong>, en interaction directe avec <code>FenetrePrincipale</code>.


#### <div style="color:#FFB347;">Attributs principaux</div>
- <code>CHEMIN_SAUVEGARDES</code> : chemin des sauvegardes.<br>
- <code>fenetrePrincipale</code> : référence à la fenêtre principale.<br>
- <code>cheminDossiers</code> : chemin des dossiers projets.<br>
- <code>panelProjets</code> : panneau contenant les projets.

---

#### <div style="color:#FFB347;">PanneauProjets (constructeur)</div>
<div style="color:#5D8AA8;">
Initialise le panneau, configure l’interface graphique, charge la liste des projets et installe les actions utilisateur.
</div>

---

#### <div style="color:#FFB347;">actualiser</div>
<div style="color:#5D8AA8;">
Vide le panneau des projets puis recharge la liste depuis le fichier de configuration et met à jour l’affichage.
</div>

---

#### <div style="color:#FFB347;">chargerProjets</div>
<div style="color:#5D8AA8;">
Lit le fichier contenant tous les chemins des projets (<code>projets.xml</code>), valide les chemins, récupère les intitulés et crée dynamiquement les boutons correspondant aux projets existants.
</div>

---

#### <div style="color:#FFB347;">Méthodes secondaires</div>
<div style="color:#5D8AA8;">
- <strong>créerBoutonProjet(...)</strong> : crée un bouton de projet avec menu contextuel et action d’ouverture via <code>fenetrePrincipale</code>.<br>
- <strong>renommerProjet(...)</strong> : demande un nouvel intitulé puis met à jour le projet dans le fichier et les sauvegardes associées.<br>
- <strong>supprimerProjet(...)</strong> : supprime un projet de la liste et efface sa sauvegarde après confirmation utilisateur.<br>
- <strong>intituleExiste(...)</strong> : vérifie si un intitulé de projet existe déjà dans le fichier des projets.<br>
- <strong>modifierProjetDansFichier(...)</strong> : modifie le fichier <code>projets.xml</code> pour renommer ou supprimer un projet et gère les fichiers de sauvegarde.
</div>

<br><br>

### <div style="color:#E74C3C;">PanneauDiagramme</div>

Cette classe représente le <strong>panneau central du diagramme UML</strong>.
Elle gère l’affichage des blocs de classes, des liaisons et toutes les interactions utilisateur (clic, drag, zoom, pan).
Elle constitue le <strong>cœur graphique et interactif</strong> de l’application.


#### <div style="color:#FFB347;">Attributs principaux</div>
- <code>lstBlocsClasses</code> : liste des blocs de classes.<br>
- <code>lstLiaisons</code> : liste des liaisons.<br>
- <code>fenetrePrincipale</code> : référence à la fenêtre principale.<br>
- <code>cheminProjetCourant</code> : chemin du projet courant.<br>
- <code>zoomLevel</code> : niveau de zoom.<br>
- <code>panOffsetX/Y</code> : décalage du pan horizontal/vertical.

---

#### <div style="color:#FFB347;">PanneauDiagramme (constructeur)</div>
<div style="color:#5D8AA8;">
Initialise le panneau, configure le menu contextuel, les paramètres graphiques et installe tous les listeners d’interaction.
</div>

---

#### <div style="color:#FFB347;">chargerProjet</div>
<div style="color:#5D8AA8;">
Charge un projet UML, récupère les blocs et liaisons via <code>FenetrePrincipale</code>, initialise leurs dépendances et rafraîchit l’affichage.
</div>

---

#### <div style="color:#FFB347;">optimiserPositionsClasses</div>
<div style="color:#5D8AA8;">
Organise automatiquement les blocs en grille, recalcule les ancrages et ajuste les liaisons pour éviter les chevauchements.
</div>

---

#### <div style="color:#FFB347;">optimiserPositionsLiaisons</div>
<div style="color:#5D8AA8;">
Recalcule les ancrages de toutes les liaisons en utilisant la liste complète des liaisons puis redessine le diagramme.
</div>

---

#### <div style="color:#FFB347;">Méthodes secondaires</div>
<div style="color:#5D8AA8;">
- <strong>ajouterListenersInteraction()</strong> : installe les listeners souris pour clic, drag, zoom, pan et menus contextuels.<br>
- <strong>organiserEnGrille()</strong> : positionne les blocs de classes dans une grille régulière selon leur nombre et leur taille.<br>
- <strong>optimiserAncragesPourLiaison(...)</strong> : calcule dynamiquement les côtés d’ancrage optimaux entre deux blocs.<br>
- <strong>determinerMeilleurCote(...)</strong> : détermine le côté de sortie d’une liaison selon la position relative des blocs.<br>
- <strong>determinerMeilleurCoteDestination(...)</strong> : détermine le côté d’entrée opposé pour la destination d’une liaison.<br>
- <strong>paintComponent(...)</strong> : applique le zoom et le pan puis dessine les liaisons et les blocs du diagramme.<br>
- <strong>dessinerLiaisons(...)</strong> : appelle <code>liaison.dessiner(...)</code> pour chaque liaison du diagramme.<br>
- <strong>afficherZoomPercentage(...)</strong> : affiche le pourcentage de zoom dans l’interface graphique.<br>
- <strong>modifieMultiplicite(...)</strong> : modifie la multiplicité d’une liaison en fonction de son origine ou destination.<br>
- <strong>modifierRole(...)</strong> : modifie le rôle d’une liaison (origine ou destination) à partir de son identifiant.<br>
- <strong>rafraichirDiagramme()</strong> : force la mise à jour complète de l’affichage Swing.<br>
- <strong>actionSauvegarder()</strong> : délègue la sauvegarde du diagramme à <code>fenetrePrincipale.sauvegarderClasses(...)</code>.<br>
- <strong>actionEffectuee()</strong> : déclenche une sauvegarde automatique si l’option est activée.<br>
- <strong>viderDiagramme()</strong> : supprime tous les blocs et liaisons puis rafraîchit l’affichage.
</div>

<br><br>


### <div style="color:#E74C3C;">BlocClasse</div>

Cette classe représente <strong>l’affichage graphique d’une classe UML</strong> dans le diagramme.
Elle gère la représentation visuelle (nom, attributs, méthodes), les modes condensé / plein écran et les interactions géométriques.
Elle constitue l’<strong>unité visuelle de base</strong> manipulée par le panneau de diagramme.


#### <div style="color:#FFB347;">Attributs principaux</div>
- `id` : identifiant du bloc.
- `nom` : nom de la classe UML.
- `attributs` : liste des attributs.
- `methodes` : liste des méthodes.
- `x` : position horizontale.
- `y` : position verticale.
- `largeur` : largeur du bloc.
- `hauteur` : hauteur du bloc.
- `affichagePleinEcran` : mode d’affichage actif.

---

#### <div style="color:#FFB347;">BlocClasse (constructeur)</div>
<div style="color:#5D8AA8;">
Initialise un bloc UML avec un identifiant unique, une position initiale, des dimensions par défaut et des listes vides d’attributs et de méthodes.
</div>

---

#### <div style="color:#FFB347;">dessiner</div>
<div style="color:#5D8AA8;">
Dessine intégralement le bloc de classe : fond, en-tête, nom, attributs, méthodes, séparateurs et styles selon l’état (interface, sélection, affichage).
</div>

---

#### <div style="color:#FFB347;">calculerHauteur</div>
<div style="color:#5D8AA8;">
Calcule dynamiquement la hauteur réelle du bloc en fonction du contenu affiché, du mode d’affichage et des retours à la ligne.
</div>

---

#### <div style="color:#FFB347;">Méthodes secondaires</div>
- <div style="color:#5D8AA8;"><strong>formatMethodeAvecLargeur(...)</strong> : formate une méthode sur plusieurs lignes selon la largeur disponible et le mode d’affichage.</div>
- <div style="color:#5D8AA8;"><strong>formatMethode(...)</strong> : limite le nombre de paramètres affichés en mode condensé.</div>
- <div style="color:#5D8AA8;"><strong>getAttributsAffichage()</strong> : retourne la liste d’attributs à afficher selon le mode plein écran ou condensé.</div>
- <div style="color:#5D8AA8;"><strong>getMethodesAffichage()</strong> : retourne la liste de méthodes à afficher avec formatage et limitation éventuelle.</div>
- <div style="color:#5D8AA8;"><strong>contient(int px, int py)</strong> : vérifie si un point donné se situe à l’intérieur du bloc.</div>
- <div style="color:#5D8AA8;"><strong>chevaucheTexte(...)</strong> : détecte si un rectangle de texte chevauche la zone du bloc.</div>
- <div style="color:#5D8AA8;"><strong>deplacer(int dx, int dy)</strong> : déplace le bloc en modifiant ses coordonnées X et Y.</div>

<br>

## <u>Package vue.liaison</u>

Le package **liaison** gère tout ce qui concerne l'affichage et le calcul des liaisons entre les blocs de classes.
Il est composé de plusieurs classes helper qui délèguent les responsabilités spécifiques.


---

### <div style="color:#E74C3C;">LiaisonVue</div>


Cette classe gère **l'affichage visuel des liens** entre deux <code>BlocClasse</code>.
Elle calcule les chemins orthogonaux, gère les ancrages, détecte les intersections et dessine les flèches et multiplicités UML.

#### <div style="color:#FFB347;">Attributs principaux</div>

- `id` : `UUID` - Identifiant unique de la liaison
- `type` : `String` - Type de liaison (heritage, association_uni, association_bi, interface)
- `blocOrigine` : `BlocClasse` - Bloc source de la liaison
- `blocDestination` : `BlocClasse` - Bloc destination de la liaison
- `ancrageOrigine` : `Point` - Point d'ancrage sur le bloc origine
- `ancrageDestination` : `Point` - Point d'ancrage sur le bloc destination
- `roleOrig` : `String` - Rôle côté origine
- `roleDest` : `String` - Rôle côté destination
- `multOrig` : `String` - Multiplicité côté origine
- `multDest` : `String` - Multiplicité côté destination
- `unidirectionnel` : `boolean` - Indique si l'association est unidirectionnelle


---

#### <div style="color:#FFB347;">LiaisonVue(...) (constructeurs)</div>

<div style="color:#5D8AA8;">
Initialise une liaison avec les blocs origine/destination, le type et optionnellement les multiplicités.<br>
Appelle automatiquement <code>chooseBestSides()</code> pour calculer le meilleur chemin.
</div>

---

#### <div style="color:#FFB347;">chooseBestSides()</div>

<div style="color:#5D8AA8;">
Choisit les meilleurs côtés et positions d'ancrage pour minimiser le nombre de segments et éviter les collisions.<br>
Teste toutes les combinaisons de côtés et positions, évalue chaque chemin et sélectionne le meilleur selon plusieurs critères :<br>
- <strong>Nombre de segments</strong> (critère principal)<br>
- <strong>Absence d'accordéons</strong> (allers-retours)<br>
- <strong>Distance totale</strong> du chemin<br><br>
Pour les liaisons multiples entre les mêmes blocs, applique un décalage pour éviter la superposition.
</div>

---

#### <div style="color:#FFB347;">calculerCheminOptimal()</div>

<div style="color:#5D8AA8;">
Recalcule le chemin de la liaison en utilisant <code>CalculateurChemin</code>.<br>
Gère la détection des intersections avec les autres liaisons et applique le rendu avec des ponts si nécessaire.
</div>

---

#### <div style="color:#FFB347;">dessiner(Graphics2D g, double zoom, int panX, int panY)</div>

<div style="color:#5D8AA8;">
Dessine la liaison sur le panneau diagramme avec zoom et panoramique.<br>
Trace les segments du chemin, ajoute les flèches selon le type (héritage, interface, association), affiche les multiplicités et rôles.
</div>

---

#### <div style="color:#FFB347;">Méthodes secondaires</div>

<div style="color:#5D8AA8;">
- <strong>setAncrageOrigine(...)</strong> : définit le point d'ancrage origine<br>
- <strong>setAncrageDestination(...)</strong> : définit le point d'ancrage destination<br>
- <strong>setTousLesBlocs(...)</strong> : met à jour la liste des blocs pour la détection d'obstacles<br>
- <strong>setToutesLesLiaisons(...)</strong> : met à jour la liste des liaisons pour la détection d'intersections<br>
- <strong>getId()</strong> : retourne l'identifiant unique<br>
- <strong>getType()</strong> : retourne le type de liaison<br>
- <strong>getBlocOrigine()</strong>, <strong>getBlocDestination()</strong> : retournent les blocs liés<br>
- <strong>getRoleOrig()</strong>, <strong>getRoleDest()</strong> : retournent les rôles<br>
- <strong>getMultOrig()</strong>, <strong>getMultDest()</strong> : retournent les multiplicités<br>
- <strong>setRoleOrig(...)</strong>, <strong>setRoleDest(...)</strong> : modifient les rôles<br>
- <strong>setMultOrig(...)</strong>, <strong>setMultDest(...)</strong> : modifient les multiplicités<br>
- <strong>isUnidirectionnel()</strong> : indique si la liaison est unidirectionnelle
</div>

<br>

---

### <div style="color:#E74C3C;">CalculateurChemin</div>

Cette classe calcule les **chemins orthogonaux** pour les liaisons entre blocs.
Elle génère des chemins composés de segments horizontaux et verticaux uniquement, en évitant les obstacles.


#### <div style="color:#FFB347;">Attribut</div>

- `detecteurObstacles` : `DetecteurObstacles` - Instance pour détecter les collisions


---

#### <div style="color:#FFB347;">creerCheminOrthogonal(Point debut, Point fin, int coteDebut, int coteFin)</div>

<div style="color:#5D8AA8;">
Crée un chemin orthogonal entre deux points en fonction des côtés de sortie et d'entrée.<br>
<strong>Système de côtés :</strong> 0=HAUT, 1=DROITE, 2=BAS, 3=GAUCHE<br><br>

<strong>Algorithme :</strong><br>
1. Calcule les points de sortie et d'entrée avec une marge interne (30 pixels)<br>
2. Détecte les côtés opposés (haut-bas, gauche-droite) pour optimiser<br>
3. Si alignés parfaitement, trace une ligne droite<br>
4. Sinon, calcule des points intermédiaires pour créer un chemin en escalier<br>
5. Nettoie les points redondants (3 points alignés → 2 points)<br><br>

<strong>Retourne :</strong> Liste de points formant le chemin
</div>

---
#### <div style="color:white;"><u>Méthodes</u></div>

#### <div style="color:#FFB347;">calculerLongueurChemin(List&lt;Point&gt; chemin)</div>

<div style="color:#5D8AA8;">
Calcule la longueur totale d'un chemin en additionnant les distances entre chaque point.<br>
<strong>Retourne :</strong> Distance totale en pixels
</div>

---

#### <div style="color:#FFB347;">cheminADesCollisions(List&lt;Point&gt; chemin)</div>

<div style="color:#5D8AA8;">
Vérifie si un chemin traverse des blocs obstacles en testant chaque segment.<br>
<strong>Retourne :</strong> <code>true</code> si collision détectée, <code>false</code> sinon
</div></div>

---

#### <div style="color:#FFB347;">Méthodes secondaires</div>

<div style="color:#5D8AA8;">
- <strong>calculerPointDecale(Point pt, int cote, int margeInterne)</strong> : calcule un point décalé selon le côté et la marge<br>
- <strong>nettoyerPointsRedondants(List&lt;Point&gt; chemin)</strong> : supprime les points intermédiaires alignés<br>
- <strong>eviterCoin(int coord1, int coord2, int reference, boolean vertical)</strong> : calcule une position qui évite les coins de blocs
</div>

<br>

---
    
### <div style="color:#E74C3C;">DetecteurObstacles</div>

Cette classe détecte les **obstacles** (autres blocs) pour le routage des liaisons.
Elle permet de vérifier si un segment traverse un bloc et d'obtenir la liste des obstacles sur un trajet.


#### <div style="color:#FFB347;">Attributs</div>

- `blocOrigine` : `BlocClasse` - Bloc source (ignoré dans la détection)
- `blocDestination` : `BlocClasse` - Bloc destination (ignoré dans la détection)
- `tousLesBlocs` : `List<BlocClasse>` - Liste de tous les blocs du diagramme


---

#### <div style="color:#FFB347;">aUnObstacle(boolean estHorizontal, int a1, int a2, int b)</div>

<div style="color:#5D8AA8;">
Détecte si un segment horizontal ou vertical traverse des obstacles.<br><br>

<strong>Paramètres :</strong><br>
- <code>estHorizontal</code> : true pour segment horizontal, false pour vertical<br>
- <code>a1, a2</code> : coordonnées de début et fin sur l'axe principal<br>
- <code>b</code> : coordonnée sur l'axe perpendiculaire<br><br>

<strong>Retourne :</strong> <code>true</code> si obstacle détecté
</div>

---

#### <div style="color:#FFB347;">aObstacleHorizontalStrict(int x1, int x2, int y)</div>

<div style="color:#5D8AA8;">
Détection <strong>stricte</strong> : vérifie qu'une ligne horizontale ne traverse <strong>aucun</strong> bloc.<br>
Plus restrictif que <code>aUnObstacle</code>, utilisé pour les vérifications finales.<br><br>

<strong>Retourne :</strong> <code>true</code> si collision détectée
</div>

---

#### <div style="color:#FFB347;">aObstacleVerticalStrict(int x, int y1, int y2)</div>

<div style="color:#5D8AA8;">
Détection <strong>stricte</strong> : vérifie qu'une ligne verticale ne traverse <strong>aucun</strong> bloc.<br><br>

<strong>Retourne :</strong> <code>true</code> si collision détectée
</div>

---

#### <div style="color:#FFB347;">Méthodes secondaires</div>

<div style="color:#5D8AA8;">
- <strong>getObstaclesSurLigneHorizontale(int x1, int x2, int y)</strong> : retourne la liste des blocs traversés par une ligne horizontale<br>
- <strong>getObstaclesSurLigneVerticale(int x, int y1, int y2)</strong> : retourne la liste des blocs traversés par une ligne verticale<br>
- <strong>detecterCollisionAvecBloc(...)</strong> : vérifie si un point est à l'intérieur d'un bloc avec marge
</div>
</div>
</div>
</div>

<br>

---

### <div style="color:#E74C3C;">GestionnaireAncrage</div>

Cette classe gère les points **d'ancrage** et les positions sur les blocs de classes.
Elle permet de calculer les points pour les liaisons, les multiplicités et les rôles.


#### <u>Méthodes</u>
#### <div style="color:#FFB347;">getPointSurCote(BlocClasse bloc, int cote, double posRel)</div>

<div style="color:#5D8AA8;">
Renvoie un point sur un côté d'un bloc.<br><br>

<strong>Paramètres :</strong><br>
- <code>bloc</code> : Bloc cible<br>
- <code>cote</code> : Côté (0=HAUT, 1=DROITE, 2=BAS, 3=GAUCHE)<br>
- <code>posRel</code> : Position relative sur le côté (0.0 à 1.0)<br><br>

<strong>Retourne :</strong> Point exact sur le côté
</div>

---

#### <div style="color:#FFB347;">getCoteLePlusProche(Point souris, BlocClasse bloc)</div>

<div style="color:#5D8AA8;">
Renvoie le côté le plus proche d'un point donné (pour les interactions souris).<br><br>

<strong>Retourne :</strong> Numéro du côté le plus proche
</div>

---

#### <div style="color:#FFB347;">getPosRelativeDepuisSouris(Point souris, BlocClasse bloc, int cote)</div>

<div style="color:#5D8AA8;">
Calcule la position relative d'un point sur un côté du bloc (pour placer un ancrage).<br><br>

<strong>Retourne :</strong> Position normalisée entre 0.0 et 1.0
</div>

---

#### <div style="color:#FFB347;">Méthodes secondaires</div>

<div style="color:#5D8AA8;">
- <strong>estSurAncrage(Point ancrage, Point souris, ...)</strong> : vérifie si la souris est sur un point d'ancrage<br>
- <strong>calculerPositionMultiplicite(Point a, int cote, ...)</strong> : calcule où afficher la multiplicité<br>
- <strong>calculerPositionRole(Point a, int cote, ...)</strong> : calcule où afficher le rôle<br>
- <strong>calculerPrioriteCentre(double pos)</strong> : détermine la priorité d'une position pour le centrage
</div>
</div>
</div>
</div>


<br>

---

### <div style="color:#E74C3C;">GestionnaireIntersections</div>


Cette classe gère la <strong>détection des intersections</strong> entre liaisons.<br>
Elle permet de détecter les croisements et les chevauchements pour gérer le rendu avec des ponts.


<br>

#### <div style="color:#FFB347;">getIntersectionSegment(Point a1, Point a2, Point b1, Point b2)</div>

<div style="color:#5D8AA8;">
Détecte si deux segments orthogonaux se croisent et retourne le point d'intersection.<br><br>

<strong>Retourne :</strong> Point d'intersection ou <code>null</code> si pas d'intersection
</div>

---

#### <div style="color:#FFB347;">cheminsPartagentSegments(List&lt;Point&gt; chemin1, List&lt;Point&gt; chemin2)</div>

<div style="color:#5D8AA8;">
Vérifie si deux chemins partagent des segments communs (chevauchement).<br><br>

<strong>Retourne :</strong> <code>true</code> si les chemins se chevauchent
</div>

---

#### <div style="color:#FFB347;">Méthodes secondaires</div>

<div style="color:#5D8AA8;">
- <strong>segmentsSeChevauchent(Point a1, Point a2, Point b1, Point b2)</strong> : vérifie si deux segments sont colinéaires et se chevauchent
</div>
</div>
</div>

<br>

---

### <div style="color:#E74C3C;">RenduLiaison</div>


Cette classe gère le <strong>rendu graphique</strong> des liaisons.<br>
Elle dessine les lignes avec ponts aux intersections, les flèches et les symboles UML.

<br>

#### <div style="color:#FFB347;">Attribut</div>

<div style="color:#5D8AA8;">
- <code>gestionnaireIntersections</code> : <code>GestionnaireIntersections</code> - Instance pour gérer les intersections
</div>

---


#### <u>Méthodes</u>

#### <div style="color:#FFB347;">void  dessinerLigneAvecPonts(Graphics2D g, Point p1, Point p2, List&lt;Point&gt; intersections, Stroke traitNormal)</div>

<div style="color:#5D8AA8;">
Dessine une ligne avec des <strong>ponts</strong> (arcs) aux points d'intersection.<br>
Les ponts permettent de visualiser qu'une liaison passe par-dessus une autre.
</div>

---

#### <div style="color:#FFB347;">void dessinerFlecheVide(Graphics2D g, Point a, int s)</div>

<div style="color:#5D8AA8;">
Dessine une <strong>flèche vide</strong> (triangle non rempli) pour l'héritage ou l'interface.<br>
<strong>Système de côtés :</strong> 0=HAUT, 1=DROITE, 2=BAS, 3=GAUCHE
</div>

---

#### <div style="color:#FFB347;">void dessinerFlecheAssociation(Graphics2D g, Point a, int s)</div>

<div style="color:#5D8AA8;">
Dessine une <strong>flèche d'association</strong> (2 lignes formant un V) pour les associations unidirectionnelles.
</div>


<br><br>

### <div style="color:#E74C3C;">BarreMenus</div>


Cette classe représente la <strong>barre de menu principale</strong> de l’application.
Elle fournit un <strong>accès</strong> aux <strong>outils d’affichage</strong>, d’<strong>édition</strong>, de <strong>gestion des fichiers</strong> et à l’<strong>aide</strong>.
Hérite de <strong>JMenuBar</strong> pour être directement intégrée dans la fenêtre principale.


<br>

#### <div style="color:#FFB347;">Attributs principaux</div>
- `fenetrePrincipale` : référence à la fenêtre principale.
- items de menu (`JCheckBoxMenuItem`) : options d’affichage et de sauvegarde.

---

#### <div style="color:#FFB347;">BarreMenus (constructeur)</div>
<div style="color:#5D8AA8;">
Initialise la barre de menu avec :<br>
- Couleur de fond personnalisée,<br>
- Menus principaux (<strong>Fichier</strong>, <strong>Affichage</strong>, <strong>Aide</strong>),<br>
- Style graphique (couleurs, police, opacité),<br>
- Liaison des actions aux événements des items de menu.
</div>

---

#### <div style="color:#FFB347;">Méthodes secondaires</div>
- <div style="color:#5D8AA8;"><strong>creerMenuFichier()</strong> : crée le menu "Fichier" avec les options Ouvrir projet, Exporter en image, Sauvegarder, Quitter.</div>
- <div style="color:#5D8AA8;"><strong>creerMenuAffichage()</strong> : crée le menu "Affichage" avec options Afficher attributs, Afficher méthodes, Optimiser positions.</div>
- <div style="color:#5D8AA8;"><strong>creerMenuAide()</strong> : crée le menu "Aide" avec l’item À propos.</div>

- <div style="color:#5D8AA8;"><strong>actionOuvrirProjet()</strong> : ouvre un dialogue pour sélectionner un projet et initie sa vérification et son chargement.</div>
- <div style="color:#5D8AA8;"><strong>verifierFichiersProjet(String cheminFichier)</strong> : affiche un message d’avertissement si des fichiers invalides sont détectés.</div>
- <div style="color:#5D8AA8;"><strong>sauvegardeProjetXml(String cheminFichier)</strong> : transmet le chemin au contrôleur pour charger le projet.</div>
- <div style="color:#5D8AA8;"><strong>actionAffichageAttributs()</strong> : met à jour l’affichage des attributs dans la fenêtre principale.</div>
- <div style="color:#5D8AA8;"><strong>actionAffichageMethodes()</strong> : met à jour l’affichage des méthodes dans la fenêtre principale.</div>
- <div style="color:#5D8AA8;"><strong>actionSauvegardeAuto()</strong> : active ou désactive la sauvegarde automatique.</div>
- <div style="color:#5D8AA8;"><strong>actionOptimiser()</strong> : optimise la position des classes et des liaisons dans le diagramme.</div>
- <div style="color:#5D8AA8;"><strong>actionOptimiserLiaisons()</strong> : optimise uniquement la position des liaisons.</div>
- <div style="color:#5D8AA8;"><strong>actionSauvegarder()</strong> : déclenche la sauvegarde manuelle du projet.</div>
- <div style="color:#5D8AA8;"><strong>actionAPropos()</strong> : affiche une boîte de dialogue HTML avec les auteurs et informations sur le projet.</div>

---

## <u>Package vue.role_class</u>

### <div style="color:#E74C3C;">FenetreChangementMultiplicite</div>

<div style="color:#5D8AA8;">
Hérite de <strong>JFrame</strong>. Fenêtre pour modifier les multiplicités d’un diagramme UML.
</div>
</div>
</div>

---

### <div style="color:#E74C3C;">PanneauModif</div>

Cette classe représente le <strong>panneau de modification des multiplicités</strong> d’une classe dans le diagramme UML.<br>
Elle permet à l’utilisateur de sélectionner une liaison, saisir les multiplicités minimum et maximum, puis de valider ou annuler les changements.<br>
Elle agit comme <strong>interface entre l’IHM et les objets LiaisonVue</strong>, en appliquant les changements directement sur le diagramme.

---

#### <div style="color:#FFB347;">Attributs principaux</div>
- `blocSelectionne` : Bloc actuellement sélectionné.
- `panDiag` : Référence au panneau de diagramme principal.
- `listeLiaisonsIHM` : Liste des liaisons affichées dans l’IHM.
- `txtMultipliciteMin` : Champ de texte pour la multiplicité minimum.
- `txtMultipliciteMax` : Champ de texte pour la multiplicité maximum.
- `btnValider` : Bouton pour valider les modifications.
- `btnAnnuler` : Bouton pour annuler les modifications.

---

#### <div style="color:#FFB347;">Méthodes secondaires</div>
- <div style="color:#5D8AA8;"><strong>caractereValideMultMin(String min)</strong> : vérifie si min est un entier.</div>
- <div style="color:#5D8AA8;"><strong>caractereValideMultMax(String max)</strong> : vérifie si max est un entier.</div>
- <div style="color:#5D8AA8;"><strong>actionPerformed(ActionEvent e)</strong> : gère les clics sur les boutons valider et annuler.</div>
- <div style="color:#5D8AA8;"><strong>getLiaisonConnectees(BlocClasse blcClasse)</strong> : renvoie la liste des liaisons connectées à la classe donnée.</div>


---



### <div style="color:#E74C3C;">FenetreModifRole</div>

Cette classe représente la **fenêtre de modification des rôles** dans le diagramme UML.
Elle encapsule le panneau `PanneauModifRole` et sert de conteneur Swing pour permettre à l’utilisateur de modifier les rôles d’une liaison.
Elle constitue la **fenêtre modale pour l’édition des rôles**, centrée sur l’écran et de taille fixe.


---

#### <div style="color:#FFB347;">Attributs principaux</div>

- `panDiag` : Référence au panneau de diagramme principal.
- `PanneauModifRole` : Instance du panneau de modification des rôles.


---

#### <div style="color:#FFB347;">FenetreModifRole (constructeur)</div>
<div style="color:#5D8AA8;">
Initialise la fenêtre avec un titre, une taille fixe et une position centrée à l’écran.
Crée le panneau `PanneauModifRole` et l’ajoute au centre de la fenêtre via `BorderLayout`.
</div>


---

#### <div style="color:#FFB347;">PanneauModifRole (constructeur)</div>
<div style="color:#5D8AA8;">
Initialise le panel, configure le titre et le nom du bloc sélectionné, crée le champ de texte pour le rôle, installe les boutons Valider/Annuler et le panel scrollable contenant la liste des liaisons.
Remplit la liste des liaisons avec séparation **Associations / Interfaces** et installe les listeners pour les boutons et radio-boutons.
</div>

---

#### <u>Méthodes</u>

#### <div style="color:#FFB347;">remplirListeLiaisons</div>
<div style="color:#5D8AA8;">
Parcourt toutes les liaisons du diagramme et ajoute dans le panel uniquement celles <Strong>liées au bloc sélectionné</Strong>, séparées en <Strong>Associations et Interfaces</Strong>.
Chaque liaison est représentée par un <Strong>radio-bouton</Strong> qui remplit le champ du rôle lorsqu’il est sélectionné.
</div>

<br>

#### <div style="color:#FFB347;">ajouterLiaisonALaListe</div>
<div style="color:#5D8AA8;">
Crée une ligne avec un radio-bouton pour une liaison donnée.
Si le bloc sélectionné est l’origine, affiche le rôle origine ; si le bloc sélectionné est la destination, affiche le rôle destination.
Ajoute le radio-bouton à un `ButtonGroup` pour assurer une sélection unique.
</div>

<br>

#### <div style="color:#FFB347;">actionPerformed</div>
<div style="color:#5D8AA8;">
Gère les actions des boutons Valider et Annuler : <br>
- <strong>Valider</strong> : récupère le rôle saisi et modifie la liaison correspondante via `panDiag.modifierRole(...)`, puis rafraîchit le diagramme et ferme la fenêtre.<br>
- <strong>Annuler</strong> : ferme simplement la fenêtre sans modifier la liaison.
</div>

<br>

#### <div style="color:#FFB347;">Méthodes secondaires</div>
<div style="color:#5D8AA8;">
- <strong>getLiaisonSelectionnee()</strong> : retourne la liaison actuellement sélectionnée.<br>
- <strong>rafraichirPanel()</strong> : force la mise à jour graphique du panel et de ses composants Swing.
</div>

<br><br>

*Projet académique – IUT du Havre*

SAE 3.01 – Outil de rétroconception Java-UML

Romain BARUCHELLO, Jules BOUQUET, Pierre COIGNARD, Paul NOEL, Thibault PADOIS, Hugo VARAO GOMES DA SILVA
