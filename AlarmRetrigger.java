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
  public BRelTime getRetriggerDelay() { return (BRelTime)get("retriggerDelay"); }
  public String getRetriggerAlarmClass() { return getString("retriggerAlarmClass"); }

////////////////////////////////////////////////////////////////
// Setters
////////////////////////////////////////////////////////////////

  public void setDebug(javax.baja.status.BStatusBoolean v) { set("debug", v); }
  public void setFullQuery(String v) { setString("fullQuery", v); }
  public void setRetriggerDelay(javax.baja.sys.BRelTime v) { set("retriggerDelay", v); }
  public void setRetriggerAlarmClass(String v) { setString("retriggerAlarmClass", v); }

////////////////////////////////////////////////////////////////
// Program Source
////////////////////////////////////////////////////////////////

  /*
  Purpose: If alarms have been active and acknowledged for X period of time, 
            then retrigger the alarm taking it out of the acknowledged state. 
            
  Version: 1.1 - ✅ updated the inital draft, cleaned up the comments 
               - ✅ added added full alarm data to new rec
               - ✅ need to fix source so it also includes the name of the point and not just the ORD.            
               - � need to get action Slot working to invoke retriggerAlarms().
  
  Author: Rick Stehmeyer 
  
  Date:   1/27/2026
  
  */
  
  //GLOBALS/////////////////////////////////////////////////////////////////////////////
  /*--
  Public String QUERY is for getting the entire list of unacked alarms to be used in the program
  used the query window in niagara to generate this : local:|foxs:|alarm:|bql:select * where ackState = 'unacked'
  this string will be used in the makeList() method below. 
  --*/
  //local:|foxs:|alarm:|bql:select * where ackState = 'unacked' and sourceState = 'offnormal'
  String QUERY = "alarm:|bql:select * where ackState = 'acked' and sourceState = 'offnormal'";
  
  String alarmClassName; //this will be used to store the alarmClassName for filtering 
  
  BComponent prog; //This will be used to set the scope to this component in the niagara station on startup
  
  boolean dbug; //for later use
  boolean fullAlarm; //for if the user wants a full duplicate of the original alarm
  
  int tableSize = 0; // used to check if BITable is empty
  ///////////////////////////////////////////////////////////////////////////ENDS GLOBALS
  
  ///////////////////////////////////////////////////////////////////////////////////////
  //STANDARD NIAGARA PROGRAM OBJECT METHODS//////////////////////////////////////////////
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
     if(dbug){System.out.println("Debug on -------------Start Execution---V1.1");} //example of my style
        
     alarmClassName = new String(getRetriggerAlarmClass());//get alarm class of alarms to be retriggered. 
     retriggerAlarms(); //I intended this to be an action slot on the object, but for some reason I cant get that to fire this method, probably because I forgot something basic
     
  }//ends onExecute()
  
  public void onStop() throws Exception
  {
    // shutdown code here
  }
  
  ////////////////////////////////////////////END STANDARD NIAGARA PROGRAM OBJECT METHODS
  ///////////////////////////////////////////////////////////////////////////////////////
  
  
  //BEGIN CUSTOM METHODS////////////////////////////////////////////////////////////////////////////
  public void retriggerAlarms(){
  
    if(dbug){System.out.println("retriggerAlarms() called");} //trace message
    
    //Call makeAlarmList Method to return a table curser looking at a BITable of the alarms.
    try{
      TableCursor c = (TableCursor)makeAlarmlist(); 
      
      //parse through the BItable
      while (c.next())
      {
        if(dbug){System.out.println("----------------");}//starts while loop debug visual parser
        
        //Grab the cuurent record
        BAlarmRecord rec = (BAlarmRecord)c.get();
            
        // Get the timestamp when it last went into alarm     
        BAbsTime alarmTime = rec.getTimestamp();
        if(dbug){System.out.println("timestamp found in record: " + alarmTime.toString());}//trace message
              
        //if alarm time is not null, and not default continue
        if (alarmTime != null && alarmTime != BAbsTime.DEFAULT)
        {
          //Calculate duration
          long durationMillis = System.currentTimeMillis() - alarmTime.getMillis();
                  
          //Set Actual Duration of Unack and in alarm to BReltime named "duration"
          BRelTime duration = BRelTime.make(durationMillis);
          System.out.println("Alarm Duration: " + duration.toString() + " and user input is : " + getRetriggerDelay().toString());
          if(dbug){System.out.println("rec source: " + rec.getSource().encodeToString());}
          
          
          //Check to see if alarm duration is longer than the user input on the object
          if (duration.getMillis() > getRetriggerDelay().getMillis()){
            if(dbug){System.out.println("Alarm Duration longer than user input");}
            //call createAlarmRecord and pass it the rec.  
            createAlarmRecord(rec);
            
          }// ends retrigger IF
  
         }// ends if S
         if(dbug){System.out.println("----------------");}//ends while loop debug visual parser
        }//ends while
    }
    catch (Exception e){
      e.printStackTrace();
    }  
  }//ends retriggerAlarms()
  
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  private void createAlarmRecord(BAlarmRecord rec) throws Exception{
    // Get the Alarm Service
    BAlarmService service = (BAlarmService)Sys.getService(BAlarmService.TYPE);
    
    //for finding all the ords in the BOrdList in the record
    int ordListSize = rec.getSource().size();
    if(dbug){System.out.println("Number of Ords in the BOrdList: " + ordListSize);}
    
    /*
    if(dbug){//ordlist Printer
      for (int i = 0; i < ordListSize; i++){
        if(dbug){System.out.println("Found ORD: " + rec.getSource().get(i));}
      }
    }//ends debug Ord list printer
    */
    
    // Create a new alarm event, and set it up to match the original
    BAlarmRecord newRecord = new BAlarmRecord();
    newRecord.setSource(rec.getSource());
    newRecord.setAlarmClass(rec.getAlarmClass());
    newRecord.setAlarmData(rec.getAlarmData());    
    newRecord.setUuid(rec.getUuid().make());
    /*
    //if(dbug){System.out.println("ackstate: " + rec.getAckState().toString());}//trace message
        BFacets recAlarmData = rec.getAlarmData();
        if(dbug){System.out.println("AlarmData: " + recAlarmData.toString());}//trace message
        int count = recAlarmData.geti("count",123);
        if(dbug){System.out.println("AlarmData.count = : " + count);}//trace message
    */
    
    // Post it
    service.routeAlarm(newRecord);
    if(dbug){System.out.println("Retrigger: rec source: " + rec.getSource().encodeToString() + " has been retriggered!!!!");}
  
  }//ends createAlarmRecord() 
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  private TableCursor makeAlarmlist() throws Exception{
    
    //Define the BQL query to find points in alarm   
    BOrd fullQuery; //Create a BOrd to store the full final query. 
    //if(dbug){System.out.println("instantiate fullQuery");} //trace message
    
    //Concatenate Alarm Class name to query 
    String alarmClassQuery = QUERY + " and alarmClass = " + "'" + alarmClassName + "'";
    
    fullQuery = BOrd.make(alarmClassQuery);//Generate Full query using the QUERY Global from uptop
    if(dbug){System.out.println("Resolved Query = " + fullQuery.encodeToString());}//debug printline so I know what the query resolves to
    
    //Create a BITable called "result" and cast the FullQuery into it.
    BITable result = (BITable)fullQuery.resolve(prog).get();
    
    //used for debugging if table was empty - left names same - wasnt sure if moving the first TableCursor im processing with use would be a problem so created a new one.  I know, im sloppy. -RS 
    TableCursor dbugC = (TableCursor)result.cursor();
      while (dbugC.next()){
        //if(dbug){System.out.println("BITable Column list size = " + result.getColumns().size() + ", Should be 14");}   //vestigial debug line - feel free to remove 
        tableSize++; //quick and dirty tableSize count
        if(dbug){System.out.println("row: " + tableSize + dbugC.toString());}//ends debug if
     }//ends while
     
    if(dbug){System.out.println("numuber of rows found (tablesize) = " + tableSize);}//ends debug if
    
    if (tableSize > 0){
      
      //Create a TableCursor named "c" and cast the BITable named Result into it
      TableCursor c = (TableCursor)result.cursor();
      return c;
    
    }else {//if table size = 0 else statement
      System.out.println("BITable empty, nothing done"); 
      return dbugC; //Returns empty Table Cursor
    }//ends if not empty
    
  }//makeAlarmlist()
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  //END PROGRAM OBJECT////////////////////////////////////////////////////////////////////////////
}
