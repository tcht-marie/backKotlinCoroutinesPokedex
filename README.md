# <u>Back Kotlin For The Best Pokédex</u>

### Kotlin - Coroutines - Spring WebFlux

#### Projet chef d'oeuvre pour le passage du titre professionnel Conceptrice Développeuse d'Application.

Ce projet est full Pokémon !
Je récupère le pokédex, le détail de chaque Pokémon, les versions de jeux disponibles, les attaques et leurs détails, les objets/articles et leurs détails également.
Il y a la possibilité de se créer un compte utilisateur, de se connecter et de se déconnecter de son compte.
On peut avoir son propre Pokédex en ajoutant des Pokémons (la suppression d'un Pokémon ou de son Pokédex est possible aussi).

Les fonctionnalités and co :
* Appels asynchrones avec pagination quand nécessaire
* Gestion des erreurs
* Architecture hexagonale
* Gestion des dépendances
* Base de données PostgreSQL :
    * Gestion de création de compte, connexion et déconnexion de l'utilisateur
    * CRUD :
        * Affichage du Pokédex de l'utilisateur
        * Ajout ou suppression d'un Pokémon
        * Suppression du Pokédex entier