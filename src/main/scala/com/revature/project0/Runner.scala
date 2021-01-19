package com.revature.project0

import com.revature.project0.Cli

object Runner{
    // this is the other way to  write an entrypoint
    def main(args: Array[String]): Unit = {
    val cli = new Cli()
    cli.menu()
    }
}