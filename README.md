# SnakeGame
    
* This is a simple snake game. The snake is controlled by the arrow keys. The food is randomly generated. It includes
a map editor and some pre-defined maps.
#

* The game was tested on a Windows 11 using the Java Version 17-18. The Jar file is included in the repository

* The Jar file can be run without any additional libraries.
#

<h2>Map Editor</h2>
* The map can be saved by clicking the "Save Map" button.

     
         This Will open a Window where you can save the map.

* The map can be loaded by clicking the "Load Map" button.
* Map files also include all the other settings.

<h4>MAP inside the XML file:</h4>
The map is inside of map tag. All the cells have a default value of 0. [ value="0" ] 
If the value is 1, the cell is a wall.

<pre>
 <table>
    <tr>
     <td>
        <b>Row (row)</b>
     </td>
    <td>
        <b>Cell (Cell)</b>
     </td>
    <td>
        <b>Cell (Cell)</b>
     </td>
    <td>
        <b>Cell (Cell)</b>
     </td>
    <td>
        <b>Row (/row)</b>
     </td>
    </tr>
    <tr>
     <td>
        <b>Row (row)</b>
     </td>
    <td>
        <b>Cell (Cell)</b>
     </td>
    <td>
        <b>Cell (Cell)</b>
     </td>
    <td>
        <b>Cell (Cell)</b>
     </td>
    <td>
        <b>Row (/row)</b>
     </td>
    </tr>
</table>
</pre>



#
<h2>Settings</h2>

<h4>In (brackets) is the tag for the setting</h4>

<pre>
<table>
<tr>
<td>
<b>Option </b> <br>(options)
</td>
<td>
<tb>Info</tb>
</td>
</tr>
<tr>
<td>
<b>Game Speed</b><br> (snakeSpeed)
</td>
<td>
The speed of the game. The higher the number, the faster the game.
</td>
</tr>
<tr>
<td>
<tb><b>Snake Size </b><br>(snakeSize)</tb>
</td>
<td>
The size of the snake. The higher the number, the longer the snake. (In the start)
</td>
</tr>
<tr>
<td>
<tb><b>Point Multiplier</b> <br>(pointMultiplier)</tb>
</td>
<td>
The multiplier for the points. The higher the number, the more points you get for eating food.
</td>
</tr>
<tr>
<td>
<tb><b>Amount Of Food </b> <br>(foodAmount)</tb>
</td>
<td>
The amount of food on the map. The higher the number, the more food there is.
</td>
</tr>
<tr>
<td>
<tb><b>Snake Grow</b><br> (snakeGrow)</tb>
</td>
<td>
The Grow rate of the snake for eating food. The higher the number, the faster the snake grows.
</td>
</tr>
<tr>
<td>
<tb><b>Snake Color</b> <br>(snakeColor)</tb>
</td>
<td>
The color of the snake. ( Can only be changed in the xml file )
</td>
</tr>
<tr>
<td>
<tb><b>Food Color</b> <br>(foodColor))</tb>
</td>
<td>
The color of the food. ( Can only be changed in the xml file )
</td>
</tr>
</table>

</pre>
#

<h2> Compile </h2>

* I compiled the game with IntelliJ IDEA. The following commands were used:
* `mvn clean compile`
* `mvn package`

 
* Or it can also be run from:
* `View >>> Tool Windows >>> Maven >>> clean & package`
 

<h3>Release Links</h3>

[Download Snake v1.0.0]( https://github.com/Pereira-Luc/SnakeGame/releases/tag/v1.0.0 )
<br>
[Download Maps]( https://github.com/Pereira-Luc/SnakeGame/releases/tag/Maps )

