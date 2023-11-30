# Running
The project technically works in the IntelliJ run menu. The game will take the first character of every inputted line as input.
On *Nix, the program gets a bit smarter. First, compile a `.jar` via `mvn package` in the project root. Then, run it with `java -jar target/finalgame-0.0.1-jar-with-dependencies.jar`. This version can recognise your terminal dimensions to resize the maze and take key inputs without you pressing Enter.

* graphics and terminal handling are a pain on windows
* demo (linux plz)
* data structures:
  * hashmap (parent map when solving maze)
  * hashset (set of visited cells)
  * stack, we wrote this (cell walks)
  * tree (the entire maze sort of)
  * queue (maze solving, printing status updates)
  * 2d array (maze rendering)