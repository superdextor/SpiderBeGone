<h1>Spider Be Gone</h1>
<h4>Written by Dexter Lowery for Java</h4>
</br>
</br>
<h2>Gameplay</h2>
An onslought of spiders descends. Kill them all before getting overwhelmed. Earn points for every spider squished, poisoned, or eaten. Use the mouse to squish them and the keyboard to spray them. Play co-op with one person on mouse and the other on keyboard. Choose from four difficulties: Easy, Normal, Hard, & Extreme. Try to place 1st on the scoreboard. Don't play this late at night.
</br>
</br>
<h2>Soundtrack</h2>
<h4>by Kevin MacLeod (https://www.incompetech.com)</h4>
This game features a dynamic soundtrack which updates depending on gameplay.
<br/>
Music list:
<ul>
<li>Aitech</li>
<li>Broken Reality</li>
<li>Rising Tide (faster)</li>
<li>Tech Talk</li>
<li>I Feel You</li>
<li>Pilot Error</li>
<li>Metalmania</li>
<li>Take the Lead</li>
</ul>
</br>
</br>
<h2>Code</h2>
Made while teaching programming. This application does not include any external libraries other than what Java provides. The game runs one loop at 20 frames per second which handles logic, drawing, audio, & rendering, respectively. Logic is mostly contained inside the program's: "World", "Constants", "Hud", & "Application" files. The graphics utilize the BufferedImage and Graphics2D from Java. Most images are loaded from sprite sheets, the rest is internal. Audio is handled via Clip from Java. The program keeps all sound effects loaded and only loads one music piece at a time. This keeps the program light-weight and hopefully compatible with older machines.