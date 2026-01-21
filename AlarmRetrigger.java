/* Auto-generated ProgramImpl Code */

import java.util.*;              /* java Predefined*/
import javax.baja.nre.util.*;    /* nre Predefined*/
import javax.baja.sys.*;         /* baja Predefined*/
import javax.baja.status.*;      /* baja Predefined*/
import javax.baja.util.*;        /* baja Predefined*/
import com.tridium.program.*;    /* program-rt Predefined*/
import javax.baja.alarm.*;       /* alarm-rt User Defined*/
import javax.baja.collection.*;  /* baja User Defined*/
import javax.baja.naming.*;      /* baja User Defined*/
import javax.baja.control.*;     /* baja User Defined*/

public class ProgramImpl
  extends com.tridium.program.ProgramBase
{

////////////////////////////////////////////////////////////////
// Getters
////////////////////////////////////////////////////////////////

  public BStatusBoolean getDebug() { return (BStatusBoolean)get("debug"); }
  public String getFullQuery() { return getString("fullQuery"); }

////////////////////////////////////////////////////////////////
// Setters
////////////////////////////////////////////////////////////////

  public void setDebug(javax.baja.status.BStatusBoolean v) { set("debug", v); }
  public void setFullQuery(String v) { setString("fullQuery", v); }

////////////////////////////////////////////////////////////////
// Program Source
////////////////////////////////////////////////////////////////

  /*
  Purpose: If alarms have been active and acknowledged for X period of time, 
            then retrigger the alarm taking it out of the acknowledged state. 
            
  Version: 0.2 - because i needed to blow it up and try again
  
  Author: Rick Stehmeyer 
  
  Date:   1/21/2026
  */
  
  ///////GLOBALS///////////
  
  /*--
  Public String QUERY is for getting the entire list of unacked alarms to be used in the program
  used the query window in niagara to generate this : local:|foxs:|alarm:|bql:select * where ackState = 'unacked'
  --*/
  //local:|foxs:|alarm:|bql:select * where ackState = 'unacked' and sourceState = 'offnormal'
  static String QUERY = "alarm:|bql:select * where ackState = 'unacked' and sourceState = 'offnormal'";
  BComponent prog; //This will be used to set the scope to this component in the niagara station on startup
  boolean dbug; //for later use
  int tableSize = 0; // used to check if BITable is empty

  //START STANDARD NIAGARA PROGRAM OBJECT METHODS//////////////////////////////////////////////
  public void onStart() throws Exception
  {
    //set the scope to "this" component in the niagara station on startup
    prog = this.getComponent();
  }
  
  public void onExecute() throws Exception
  {
     //used for local debugging toggle (i typically use this to dump to station director output)
     //There is a statusBoolean slot on this object which allows folks to toggle the debug messaging on/off in app.director (old hobbits die hard)
     dbug = getDebug().getValue();
     if(dbug){System.out.println("Debug on");}
     retriggerAlarms(); //I intended this to be an action slot on the object, but for some reason I cant get that to fire this method, probably because I forgot something basic
     
  }//ends onExecute()
  
  public void onStop() throws Exception
  {
    // shutdown code here
  }
  ////////////////////////////////////////////////END STANDARD NIAGARA PROGRAM OBJECT METHODS
  /*--
  There is an action slot defined "retriggerAlarms", this is the method for it.  
  --*/
  
  public void retriggerAlarms(){
  
    if(dbug){System.out.println("retriggerAlarms() called");}
   // 1. Define the BQL query to find points in alarm
      
    
    BOrd fullQuery; //Create a BOrd to store the full final query. 
    if(dbug){System.out.println("instantiate fullQuery");}
    
    fullQuery = BOrd.make(QUERY);//Generate Full query using the QUERY Global from uptop
    
    //debug printline so I know what the query resolves to
    if(dbug){System.out.println("Resolved Query = " + fullQuery.encodeToString());}
    
    //Create a BITable called "result" and cast the FullQuery into it.
    BITable result = (BITable)fullQuery.resolve(prog).get();
    
    //used for debugging if table was empty - left names same - wasnt sure if moving the curser i'd eventually use would be a problem so created a new one.  I know, im sloppy. -RS 
    TableCursor dbugC = (TableCursor)result.cursor();
      while (dbugC.next()){
        tableSize++;
     }//ends while
     
    if(dbug){System.out.println("numuber of rows found " + tableSize);}//ends debug if
    
    
    if (tableSize > 0){
      //Create a TableCursor named "c" and cast the BITable named Result into it
      TableCursor c = (TableCursor)result.cursor();
   
      // 2. Iterate through the results
      if(dbug){System.out.println("Table Curser Created");}
      if(dbug){System.out.println("BITable Column list size = " + result.getColumns().size() + ", Should be 14");}
    
      //Start parsing through the table  
      while (c.next())
      {
        if(dbug){System.out.println("insideWhile");}
      
        //Grab the first record
        BAlarmRecord rec = (BAlarmRecord)c.get();
      
        //Dump to console for review of whats in the found record. 
        if(dbug){System.out.println("record found");}
        if(dbug){System.out.println(rec.toString());}
      
        // Get the timestamp when it last went into alarm     
        BAbsTime alarmTime = rec.getTimestamp();
        if(dbug){System.out.println("timestamp found in record: " + alarmTime.toString());}
        
        if (alarmTime != null && alarmTime != BAbsTime.DEFAULT)
        {
          //Calculate duration
          long durationMillis = System.currentTimeMillis() - alarmTime.getMillis();
          
          BRelTime duration = BRelTime.make(durationMillis);
  
          System.out.println("Duration: "+ duration.toString());
        }// ends if 
      }//ends while
      
    }else {//if table size = 0 else statement
      System.out.println("BITable empty, nothing done");
    }//ends if not empty
    
  }//ends retriggerAlarms()
}
