/******************************************************************************
 *  readme.txt template                                                   
 *  Map
 *****************************************************************************/

Name(s): Daniel Coblentz      
Login(s): N/A     
Precept #: N/A 
OS: Mac OS        
Compiler:   
Editor: VS-Code     
Hours: 2 


/******************************************************************************
 *  Explain your overall approach.
 *****************************************************************************/


/******************************************************************************
 *  Which input files did you use to test your program? Mark the
 *  ones where your answers agreed with our reference solutions and
 *  the ones where it disagreed. How long (in seconds) did your program
 *  take to solve each instance? How many vertices did it examine
 *  on average per shortest path query?
 *****************************************************************************/

Input file                Running Time (seconds)     Vertices    Agreed?
------------------------------------------------------------------------
usa-1000long.txt
usa-5000short.txt
usa-50000short.txt


/******************************************************************************
 *  Known bugs / limitations.
 *****************************************************************************/

/******************************************************************************
 *  List whatever help (if any) that you received.
 *****************************************************************************/


/******************************************************************************
 *  Describe any serious problems you encountered.                    
 *****************************************************************************/
the first issue was getting the A* search to render on the visual mapping this was an error in logic in the 'shortest path' file where i called 
dijkstra.showPath(s, d);
dijkstra.drawPath(s, d);
 and each of htose calls returns dijkstra(s, d) internally, which wipes out the results causing no result to appear on the given map

 second issue was implementing A* I kept getting the same error that there was no path avaible when i entered queries with a destinaino value above 3000 this was because
my origional implementation had a check that terminated the loop if the current node had no predecessor:
'if (pred[v] == -1) break;'
This condition was appropriate in plain Dijkstra algo when you might want to stop processing unreachable nodes. However, with A* which uses a heuristic to guide the search—it can occur that a node hasn’t been updated with a predecessor yet even though the destination is still reachable. 
This check caused the algorithm to stop too early, making it appear that the destination was unreachable and print the error message form earier.

/******************************************************************************
 *  List any other comments here. Feel free to provide any feedback   
 *  on how much you learned from doing the assignment, and whether    
 *  you enjoyed doing it.                                             
 *****************************************************************************/
running this progrma there is a line in 'shortestpath.java file' specifically the 'dijkstra.enableAStar(d);' you can comment this out and optionally change the
print statement at the bottom to view the run time of the base disktra algorithmn to see the difference in speed to find the same path or souce destination

This proejct was great and fun to do! I really enjoyed having some of hte base implemntation done and adding to it insteading having to make it from scratch
I think it would be a good improvmen tto potentially add in real API's to this project to see more real time data( i know thats lot of extra work though)