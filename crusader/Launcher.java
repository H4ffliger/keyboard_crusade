package crusader;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.Random;

import static crusader.Pathfinding.goToPosition;
import static crusader.Communication.getHeadquarters;

public class Launcher {

    //static int radioArray;
    private static ArrayList<HeadquarterEntity> hqLocations = new ArrayList<>();
    private static Integer exploreID;


    static void runLauncher(RobotController rc) throws GameActionException {

        //Creation process
        if (exploreID == null) {
            //ToDo: Change and coordinate with HQ shared array
            exploreID = new Random().nextInt(9);
            System.out.println("Rnd: " + Integer.toString(exploreID));
        }

        if(hqLocations.size()==0) {
            //Set the local hq Positions
            hqLocations=getHeadquarters(rc);
        }

        // Try to attack someone
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        if (enemies.length > 0) {
            //Attack first Launcher
            RobotInfo target=enemies[0];
            for(RobotInfo info :enemies) {
                target=info;
                if (info.getType()==RobotType.LAUNCHER||info.getType()==RobotType.DESTABILIZER) break;
            }
            MapLocation toAttack = target.location;
            if (rc.canAttack(toAttack)) {
                rc.setIndicatorString("Attacking");
                rc.attack(toAttack);
            } else {
                goToPosition(rc,toAttack);
            }
        }

        //Move with the pathfinding module
        //ToDo: Temporary exploring function for testing
        try {
           /* if (rc.getRoundNum() < 200) {
                explore(rc, exploreID);

            } else {*/
            //defendHQ(rc);
                goToPosition(rc, rc.getMapHeight()/2,rc.getMapWidth()/2);
                //returnToHomeBase(rc, hqLocations.get(0).x, hqLocations.get(0).y);

            //}
        } catch (GameActionException e) {
            System.out.println(rc.getType() + " Exception");
            e.printStackTrace();
        }
    }

    private static void defendHQ(RobotController rc) throws GameActionException{

    }
}
