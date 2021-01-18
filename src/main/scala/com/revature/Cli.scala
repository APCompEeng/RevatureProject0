package com.revature

import scala.io.StdIn
import scala.util.matching.Regex
import java.io.FileNotFoundException
import java.util.Random
import scala.collection.mutable.ArrayBuffer

/**
  * A CLI that allows the user to interact with our application
  * 
  * This Cli is a class because in the future we might provide customization options
  * that can be set when creating a new Cli instance
  * 
  * This Cli class will contain all our logic involving interacting with the user
  * we don't want all of our classes to be able to receive input from the command line
  * or write to the command line. Instead, we'll have (almost) all that happen here
  * 
  */
class Cli{
    
    /**
      * commanndArgPattern is a regular expression (regex) that will help us
      * extract the command and argument from user input on the command line
      * 
      * Regex is a tool used for pattern matching strings. Lots of languages and other tools
      * support regex. It's good to learn at least the basics, but you can also just use this
      * code for your project if you like
      */
    val commandArgPattern : Regex = "(\\w+)\\s*(.*)".r
    
    class wordDefinition(var line : Int = 0, var word : String = "", var definition : String = "", var guessed : Boolean = false) {
      //val length = word.length()
      override def toString(): String = {
                  s"""$word : $definition\" \n Found on line $line \n Has been guessed : $guessed"""
              }
    }
    /**
      * @param longestStreakValues is the list of words that are in the longest streak
      * @param currentStreakValues is the list of words that are in the current streak. If it surpasses longestStreak, it replaces it
      */
    var longestStreakValues = ArrayBuffer[String]()
    var currentStreakValues = ArrayBuffer[String]()

    // longestWord holds the longest word that the player has guessed
    var longestWord : String = ""

    // guessedWordsArray is an array that says what words have been guessed and their definitions
    var guessedWordsArray = ArrayBuffer[String]()

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
  }

    /**
      * This runs the menu, this the entrypoint to the Cli class
      * 
      * The menu will interact with the user on a loop and call other methods/classes
      * in order to achieve the results of the user's commands
      */ 

      def menu() : Unit = {
          printWelcome()
          var continueMenuLoop = true
          printOptions()
          while (continueMenuLoop){
          var rand = new Random();
          // take user input using StdIn.readLine
          // readLine is "blocking" which means that it pauses program execution while it waits for input
          // this is fine for us, but we do want to take notes
          val input = StdIn.readLine()
          // Here's an example using our regex above, feel free to just follow along with similar commands and args
          input match {
        case commandArgPattern(cmd, arg) if cmd.equalsIgnoreCase("newWord") => {
          try {
            val randDef = rand.nextInt(5000)
            val text = FileUtil.getTextContent("dictionarySmall.json")
            var textArray : Array[String] = text.split("\",")
            var guess : Array[String] = textArray(randDef).split(":")
            var wordToGuess = new wordDefinition (randDef, guess(0).replaceAll("""\"""","").replaceAll(" ",""), guess(1))
            println(wordToGuess.word)
            val usersGuess = StdIn.readLine()
            if (usersGuess.equalsIgnoreCase(wordToGuess.word))
            {
              guessedWordsArray += wordToGuess.word
              currentStreakValues += wordToGuess.word
              println("You guessed it! You have successfully guessed " 
              + guessedWordsArray.length + " words, " 
              + currentStreakValues.length + " of which have been guessed consectutively!")
              if (longestWord == "") {
                println(wordToGuess.word + " is now your new longest word!")
                longestWord = wordToGuess.word
              }
              else if(wordToGuess.word.length > longestWord.length)
              {
                println(s"You guessed a new longest word! $longestWord has been replaced by " + wordToGuess.word)
                longestWord = wordToGuess.word
              }
            } else {
              println("Sorry, that was not correct. Better luck next time!")
              currentStreakValues = ArrayBuffer()
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
          if(guessedWordsArray.isEmpty) {
            println("It looks like you haven't successfully guessed any words yet. Keep at it!")
          } else {
            printGuessedWordsArray()
          }
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
        case commandArgPattern(cmd, arg) => {
          println(s"""Failed to parse command: "$cmd" with arguments: "$arg"""")
        }
        case _ => {
          println("Failed to parse any input")
        }
      }
      if (currentStreakValues.length > longestStreakValues.length)
      {
        longestStreakValues = currentStreakValues
      }
      printOptions()
    }
    println("Thank you for playing the Dictionary Game, goodbye!")
  }

  def returnLongestWord() = {
    longestWord.length match {
      case zero if longestWord.length <= 0 => {
        println("It looks like you haven't successfully guessed any words yet. Keep at it!")
      }
      case _ => {
        println(s"The longest word you guessed was $longestWord, which was "+ longestWord.length +" characters long! Wow!")
      }
    }
  }

  def printGuessedWordsArray() = {
    println("You have guessed " + guessedWordsArray.length + " so far. Those words are: ")
      println(guessedWordsArray.mkString("\n"))
  }
  def returnCurrentStreak() = {
      println(s"Your current streak of words is ${currentStreakValues.length} words long. Those words were: ")
      println(currentStreakValues.mkString("\n"))
  }

  def returnLongestStreak() = {
      println(s"Your longest streak was ${longestStreakValues.length} words long. Those words were: ")
      println(longestStreakValues.mkString("\n"))
  }

}