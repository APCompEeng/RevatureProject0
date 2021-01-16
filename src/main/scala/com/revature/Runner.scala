package com.revature

object Runner{
    // this is the other way to  write an entrypoint
    def main(args: Array[String]): Unit = {
    val cli = new Cli()
    cli.menu()
    }
}