package com.revature.project0

import scala.io.StdIn
import scala.util.matching.Regex
import java.io.FileNotFoundException
import java.util.Random
import scala.collection.mutable.ArrayBuffer
import com.revature.project0.daos.WordDao
import com.revature.project0.model.Word

class Cli{
    
    
    val commandArgPattern : Regex = "(\\w+)\\s*(.*)".r
    
    /**
      * @param currentStreakValues is the list of words that are in the current streak. If it surpasses longestStreak, it replaces it
      */
    var currentStreakValues = ArrayBuffer[Word]()

    /**
      * prints a greeting to the user
      */
    def printWelcome(): Unit = {
        println("Welcome to the Dictionary Game!\n"+
        "The rules are simple: you will be given a definition of a word, and you'll be asked to guess the word!\n"+
        "If you can't guess the word, you can ask for a hint, or opt to skip\n"+
        "Let's begin!")
    }

    /**
      * Prints the commands available to the user
      */
    def printOptions(): Unit = {
        println("Commands available:")
        println("newWord : prints a new definition for you to guess")
        println("exit : exits Dictionary Game CLI")

        println("Bonus Commands:")
        println("guessedWords: prints a list of words you have already guessed")
        println("longestWord: prints the longest word you have guessed")
        println("longestStreak: prints the longest streak of correctly guessed words")
        println("currentStreak: prints the current streak of correctly guessed words")
  }

      def menu() : Unit = {
          printWelcome()
          var continueMenuLoop = true
          printOptions()
          while (continueMenuLoop){
          var rand = new Random();
          val input = StdIn.readLine() // take user input using StdIn.readLine
          input match {
        case commandArgPattern(cmd, arg) if cmd.equalsIgnoreCase("newWord") => {
          try {
            val randDef = rand.nextInt(5000)
            val text = FileUtil.getTextContent("dictionarySmall.json")
            var textArray : Array[String] = text.split("\",")
            var guess : Array[String] = textArray(randDef).split(":")
            var wordToGuess = new Word (randDef, guess(0).replaceAll("""\"""","").replaceAll(" ",""), guess(1),false)
            println(wordToGuess.definition + """"""")
            val usersGuess = StdIn.readLine()
            if (usersGuess.equalsIgnoreCase(wordToGuess.word))
            {
              wordToGuess.guessed = true
              WordDao.saveNewWord(wordToGuess)
              currentStreakValues += wordToGuess
              println("You guessed it! You have successfully guessed " 
              + currentStreakValues.length + " words consectutively!")
              val currentLongestWord : String = WordDao.getLongestWord
              if (WordDao.getLongestWord == "") {
                println(wordToGuess.word + " is now your new longest word!")
              }
              else if(wordToGuess.word.length > WordDao.getLongestWord.length)
              {
                println(s"You guessed a new longest word! ${WordDao.getLongestWord} has been replaced by " + wordToGuess.word)
              }
            } else {
              println("Sorry, that was not correct. Better luck next time!")
              WordDao.saveNewWord(wordToGuess)
            }
          } catch {
            case fnfe: FileNotFoundException => {
              println(s"Failed to find file: ${fnfe.getMessage}")
              println(s"""Found top level files:
              ${FileUtil.getTopLevelFiles.mkString(", ")}""")
            }
            }
        } 
        case commandArgPattern(cmd, arg) if cmd.equalsIgnoreCase("guessedWords") => {
          printGuessedWordsArray()
        }
        case commandArgPattern(cmd, arg) if cmd.equalsIgnoreCase("exit") => {
          continueMenuLoop = false
        }
        case commandArgPattern(cmd, arg) if cmd.equalsIgnoreCase("longestWord") => {
          returnLongestWord() 
        }
        case commandArgPattern(cmd, arg) if cmd.equalsIgnoreCase("longestStreak") => {
          returnLongestStreak()
        }
        case commandArgPattern(cmd, arg) if cmd.equalsIgnoreCase("currentStreak") => {
          returnCurrentStreak()
        }
        case commandArgPattern(cmd, arg) => {
          println(s"""Failed to parse command: "$cmd" with arguments: "$arg"""")
        }
        case _ => {
          println("Failed to parse any input")
        }
      }
      printOptions()
    }
    println("Thank you for playing the Dictionary Game, goodbye!")
  }

  def returnLongestWord() = {
    WordDao.getLongestWord.length match {
      case zero if WordDao.getLongestWord.length <= 0 => {
        println("\nIt looks like you haven't successfully guessed any words yet. Keep at it!\n")
      }
      case _ => {
        println(s"\nThe longest word you guessed was ${WordDao.getLongestWord}, which was "+ {WordDao.getLongestWord.length} +" characters long! Wow!\n")
      }
    }
  }

  def printWordsArray() = {
      println("These are the words you have seen so far: ")
      WordDao.getAll.foreach(println)
  }

  def printGuessedWordsArray() = {
      println("\nThese are the words you have guessed so far: ")
      WordDao.getGuessed.foreach(println)
      println("")
  }

  def returnCurrentStreak() = {
      println("\nThese are the words you have guessed consecutively: ")
      println(currentStreakValues.mkString("\n"))
      println("")
  }

  def returnLongestStreak() = {
      println(s"\nYour longest streak was ${WordDao.getStreak.length} words long. Those words were: ")
      println(WordDao.getStreak.mkString("\n"))
      println("")
  }

}