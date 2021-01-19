package com.revature.project0.daos

import com.revature.project0.model.Word
import com.revature.project0.ConnectionUtil
import scala.util.Using
import scala.collection.mutable.ArrayBuffer

/**
  * WordDao has CRUD methods for Words
  * 
  * It allows us to keep all of our database access logic in this file,
  * while still allowing the rest of our application to use Books
  * retrieved from the databasae.
  */
object WordDao {
    /**
      * Retrieves all Words from the word table in the db
      *
      * @return
      */
    def getAll() : Seq[Word] = {
        val conn = ConnectionUtil.getConnection();
        Using.Manager { use =>
            val stmt = use(conn.prepareStatement("SELECT * FROM words;"))
            stmt.execute()
            val rs = use(stmt.getResultSet())
            // lets use an ArrayBuffer, we're adding one element at a time
            val allWords : ArrayBuffer[Word] = ArrayBuffer()
            while (rs.next()) {
                allWords.addOne(Word.fromResultSet(rs))
            }
            allWords.toList
        }.get
    }

    def getGuessed () : ArrayBuffer[Word] = {
        var listOfWords = getAll()
        var guessedWords : ArrayBuffer[Word] = ArrayBuffer()
        for(i <- 0 to (listOfWords.length-1)) {  
            if(listOfWords(i).guessed) {
                guessedWords.addOne(listOfWords(i))
            }
        }
        return guessedWords
    }

    def getStreak() : ArrayBuffer[Word] = {
        var listOfWords = getAll()
        var longestStreak : ArrayBuffer[Word] = ArrayBuffer()
        var longestStreakLength : Int = 0
        var indexOfCurrentStreak : Int = 0
        var indexOfLongestStreak : Int = 0
        for(i <- 1 to (listOfWords.length-1))
        {
            if(listOfWords(i).guessed && !listOfWords(i-1).guessed) {
                indexOfCurrentStreak = i
            } else if (!listOfWords(i).guessed && listOfWords(i-1).guessed) {
                if (i - indexOfCurrentStreak > longestStreakLength)
                {
                    indexOfLongestStreak = indexOfCurrentStreak
                    longestStreakLength = i - indexOfCurrentStreak
                }
            } else if (i == listOfWords.length-1 && listOfWords(i-1).guessed) {
                if (i - indexOfCurrentStreak > longestStreakLength)
                {
                    indexOfLongestStreak = indexOfCurrentStreak
                    longestStreakLength = i - indexOfCurrentStreak
                }
            }
        }
        var temp : Int = indexOfLongestStreak
        while(temp < listOfWords.length && listOfWords(temp).guessed)
        {
            longestStreak.addOne(listOfWords(temp))
            temp += 1
        }
        return longestStreak
    }

    def getLongestWord : String = {
        var listOfWords = getAll()
        var longestWord : String = ""
        for(i <- 0 to listOfWords.length-1)
        {
            if(listOfWords(i).guessed && listOfWords(i).word.length > longestWord.length)
            {
                longestWord = listOfWords(i).word
            }
        }
        return longestWord
    }

    def saveNewWord(word : Word) = {
        val conn = ConnectionUtil.getConnection()
        Using.Manager { use =>
            val stmt = use(conn.prepareStatement("INSERT INTO words VALUES (?, ?, ?, ?);"))
            stmt.setInt(1,word.word_id)
            stmt.setString(2,word.word)
            stmt.setString(3,word.definition)
            stmt.setBoolean(4,word.guessed)
            stmt.execute()
            // check if rows were updated, return true if yes, false if no
            stmt.getUpdateCount() > 0
        }.getOrElse(false)
    }
}