/*
 * Copyright 2009-2011 Tilmann Z�schke. All rights reserved.
 * 
 * This file is part of ZooDB.
 * 
 * ZooDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ZooDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ZooDB.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * See the README and COPYING files for further information. 
 */
package org.zoodb.profiling.ex1;

import java.util.List;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import org.zoodb.jdo.ZooJdoHelper;
import org.zoodb.jdo.ZooJdoProperties;
import org.zoodb.profiling.api.impl.ProfilingManager;
import org.zoodb.tools.ZooHelper;
import org.zoodb.tools.impl.DataStoreManager;

/**
 * Simple example that creates a database, writes an object to it and then reads the object.
 * 
 * @author ztilmann
 */
public class Example {
    
    public static void main(String[] args) {
    	ProfilingManager.getInstance().init("example");
        
    	String dbName = "ExampleDB";
        createDB(dbName);
        populateDB(dbName);
        //readDB(dbName);
        queryDB(dbName);
        
        ProfilingManager.getInstance().finish();
        ProfilingManager.getInstance().save();
    }
    
    
	/**
     * Read data from a database.
     *  
     * @param dbName Database name.
     */
    private static void readDB(String dbName) {
        PersistenceManager pm = openDB(dbName);
        pm.currentTransaction().begin();

        Extent<ExamplePerson> ext = pm.getExtent(ExamplePerson.class);
        ExamplePerson p = ext.iterator().next();
        ext.closeAll();
        
        System.out.println("Person found: " + p.getName());
        
        pm.currentTransaction().commit();
        closeDB(pm);
    }
    
    
    /**
     * Read data from a database (query).
     *  
     * @param dbName Database name.
     */
    private static void queryDB(String dbName) {
        PersistenceManager pm = openDB(dbName);
        pm.currentTransaction().begin();
        System.out.println("QStart");
        Query q = pm.newQuery(ExamplePerson.class);
        q.setFilter("name == nameParam");
        q.declareParameters("String nameParam");
        List<ExamplePerson> res = (List<ExamplePerson>) q.execute("Fred");

        for (ExamplePerson p : res) {
        	ExampleAddress ea = p.getAddress();
        	
        	//System.out.println("Person found: " + p.getName());
        	ExampleCity ec = ea.getCity();
        	System.out.println("lives in: " + ec.getName());
        	System.out.println("lives in: " + ec.getName());
         }
 
       
        pm.currentTransaction().commit();
        pm.currentTransaction().begin();
        
        res = (List<ExamplePerson>) q.execute("Tobias");

        for (ExamplePerson p : res) {
        	ExampleAddress ea = p.getAddress();
        	System.out.println("Person found: " + p.getName());
        	ExampleCity ec = ea.getCity();
        	System.out.println("lives in: " + ec.getName());
         }



        pm.currentTransaction().commit();
        closeDB(pm);
    }
    
    
    /**
     * Populate a database.
     * 
     * @param dbName Database name.
     */
    private static void populateDB(String dbName) {
        PersistenceManager pm = openDB(dbName);
        pm.currentTransaction().begin();
        
        // define schema
        ZooJdoHelper.schema(pm).addClass(ExamplePerson.class);
        ZooJdoHelper.schema(pm).addClass(ExampleAddress.class);
        ZooJdoHelper.schema(pm).addClass(ExampleCity.class);
        
        ExamplePerson fred = new ExamplePerson("Fred");
        fred.setCities(new ExampleCity[] {new ExampleCity("c1"),new ExampleCity("c2")});
        fred.setData(new int[] {1,2,3,4,5,6,7,8,9});
        
        
        ExampleCity ec = new ExampleCity("Zurich");
        ExampleAddress ea1 = new ExampleAddress(ec);
        ea1.setDummyName("fcity");
        fred.setAddress(ea1);

        
        // create instance
        pm.makePersistent(fred);
        
        
        pm.currentTransaction().commit();
        
        pm.currentTransaction().begin();
        
        ExamplePerson tobias = new ExamplePerson("Tobias");
        ExampleAddress ea2 = new ExampleAddress(ec);
        ea2.setDummyName("tcity");
        tobias.setAddress(ea2);
        
        pm.makePersistent(tobias);
        pm.currentTransaction().commit();
        closeDB(pm);
    }

    
    /**
     * Create a database.
     * 
     * @param dbName Name of the database to create.
     */
    private static void createDB(String dbName) {
        // remove database if it exists
        DataStoreManager dsm = ZooHelper.getDataStoreManager();
        if (dsm.dbExists(dbName)) {
            dsm.removeDb(dbName);
        }

        // create database
        // By default, all database files will be created in %USER_HOME%/zoodb
        dsm.createDb(dbName);
    }

    
    /**
     * Open a new database connection.
     * 
     * @param dbName Name of the database to connect to.
     * @return A new PersistenceManager
     */
    private static PersistenceManager openDB(String dbName) {
        ZooJdoProperties props = new ZooJdoProperties(dbName);
        PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(props);
        PersistenceManager pm = pmf.getPersistenceManager();
        return pm;
    }
    
    
    /**
     * Close the database connection.
     * 
     * @param pm The current PersistenceManager.
     */
    private static void closeDB(PersistenceManager pm) {
        if (pm.currentTransaction().isActive()) {
            pm.currentTransaction().rollback();
        }
        pm.close();
        pm.getPersistenceManagerFactory().close();
    }
    
    
    
       
}


