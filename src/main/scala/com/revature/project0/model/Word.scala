package com.revature.project0.model

import java.sql.ResultSet

case class Word(var word_id : Int, var word : String, var definition : String, var guessed : Boolean) {

}

object Word {
    /**
      * Produces a Word from a record in a ResultSet
      *
      * @param rs
      * @return
      */
    def fromResultSet(rs : ResultSet) : Word = {
        apply(
            rs.getInt("word_id"),
            rs.getString("word"),
            rs.getString("definition"),
            rs.getBoolean("guessed")
        )
    }
}

/*
class wordDefinition(var line : Int = 0, var word : String = "", var definition : String = "", var guessed : Boolean = false) {
      //val length = word.length()
      override def toString(): String = {
                  s"""$word : $definition\" \n Found on line $line \n Has been guessed : $guessed"""
              }
    }
    */