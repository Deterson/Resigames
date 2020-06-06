package com;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestJDBC
{
    private List<String> messages = new ArrayList<>();

    public List<String> executerTests( HttpServletRequest request ) {
        /* Chargement du driver JDBC pour MySQL */
        try {
            messages.add( "Chargement du driver..." );
            Class.forName( "com.mysql.cj.jdbc.Driver" );
            messages.add( "Driver chargé !" );
        } catch ( ClassNotFoundException e ) {
            messages.add( "Erreur lors du chargement : le driver n'a pas été trouvé dans le classpath ! <br/>"
                    + e.getMessage() );
        }

        /* Connexion à la base de données */
        String url = "jdbc:mysql://localhost:3306/resigames_dev?serverTimezone=Europe%2FParis";
        String utilisateur = "resigames";
        String motDePasse = System.getenv("DB_PASS");
        if (motDePasse == null)
            System.err.println("DB_PASS NON SPÉCIFIÉ DANS L'ENVIRONNEMENT JAVA");
        Connection connexion = null;
        Statement statement = null;
        ResultSet resultat = null;
        try {
            messages.add( "Connexion à la base de données..." );
            connexion = DriverManager.getConnection( url, utilisateur, motDePasse );
            messages.add( "Connexion réussie !" );

            /* Création de l'objet gérant les requêtes */
            statement = connexion.createStatement();
            messages.add( "Objet requête créé !" );

            /* Exécution d'une requête de lecture */
            resultat = statement.executeQuery( "SELECT * FROM country;" );
            messages.add( "Requête \"SELECT * FROM country\" effectuée !" );

            /* Récupération des données du résultat de la requête de lecture */
            while ( resultat.next() ) {
                String name = resultat.getString( "Name" );
                String continent= resultat.getString( "Continent" );
                /* Formatage des données pour affichage dans la JSP finale. */
                messages.add( "Données retournées par la requête : country = " + name + ", continent = " + continent);
            }
        } catch ( SQLException e ) {
            messages.add( "Erreur lors de la connexion : <br/>"
                    + e.getMessage() );
        } finally {
            messages.add( "Fermeture de l'objet ResultSet." );
            if ( resultat != null ) {
                try {
                    resultat.close();
                } catch ( SQLException ignore ) {
                }
            }
            messages.add( "Fermeture de l'objet Statement." );
            if ( statement != null ) {
                try {
                    statement.close();
                } catch ( SQLException ignore ) {
                }
            }
            messages.add( "Fermeture de l'objet Connection." );
            if ( connexion != null ) {
                try {
                    connexion.close();
                } catch ( SQLException ignore ) {
                }
            }
        }

        return messages;
    }
}