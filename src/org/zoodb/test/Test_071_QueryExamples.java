package org.zoodb.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zoodb.test.test_071.Department;
import org.zoodb.test.test_071.EmpInfo;
import org.zoodb.test.test_071.EmpWrapper;
import org.zoodb.test.test_071.Employee;
import org.zoodb.test.test_071.Info;
import org.zoodb.test.util.TestTools;


/**
 * Examples from the JDO spec.
 * 
 * 	14.10 Examples:
 * 		The following class definitions for persistence capable classes are used in the examples:
 * 		package com.xyz.hr;
 * 		class Employee {
 * 			String name;
 * 			float salary;
 * 			Department dept;
 * 			Employee boss;
 * 		}
 * 		package com.xyz.hr;
 * 		class Department {
 * 			String name;
 * 			Collection emps;
 * 		}
 * 
 *		Java Data Objects 2.2
 *		JDO 2.2 182 October 10, 2008
 *
 * @author Tilmann Z�schke
 */
@SuppressWarnings("unchecked")
public class Test_071_QueryExamples {

	private static final String DB_NAME = "TestDb";
	
	private static final String DEP_NAME_R_AND_D = "R&D";
	
	@BeforeClass
	public static void setUp() {
		TestTools.createDb(DB_NAME);
		TestTools.defineSchema(DB_NAME, TestClass.class);
		TestTools.defineSchema(DB_NAME, Employee.class);
		TestTools.defineSchema(DB_NAME, Department.class);

		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();

		Department d1 = new Department(DEP_NAME_R_AND_D);
		Employee boss = new Employee("Big Mac", 100000, d1, null);
		pm.makePersistent(boss);
		Employee e;
		e = new Employee("Alice", 1000, d1, boss);
		pm.makePersistent(e);
		e = new Employee("Bob", 1000, d1, boss);
		pm.makePersistent(e);
		e = new Employee("Dave", 40000, d1, boss);
		pm.makePersistent(e);
		e = new Employee("Eve", 40000, d1, boss);
		pm.makePersistent(e);
		
		Employee boss2 = new Employee("Little Mac", 90000, d1, boss);
		pm.makePersistent(boss2);
		e = new Employee("Little Alice", 100, d1, boss2);
		pm.makePersistent(e);
		e = new Employee("Little Bob", 100, d1, boss2);
		pm.makePersistent(e);
		
		pm.currentTransaction().commit();
		TestTools.closePM(pm);
	}

	@Before
	public void setUpTestCase() {
		
	}
	
	@After
	public void tearDownTestCase() {
		TestTools.closePM();
	}

	/**
	 * 14.10.1 Basic query.
	 * This query selects all Employee instances from the candidate collection where the salary is 
	 * greater than the constant 30000.
	 * Note that the float value for salary is unwrapped for the comparison with the literal int 
	 * value, which is promoted to float using numeric promotion. If the value for the salary field
	 * in a candidate instance is null, then it cannot be unwrapped for the comparison, and the 
	 * candidate instance is rejected.
	 */
	@Test
	public void testQuery_14_10_1() {
		System.out.println("Testing Query()");
		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();

		Query q = pm.newQuery (Employee.class, "salary > 30000");
		Collection<Employee> emps = (Collection<Employee>) q.execute ();
//			<query name="basic">
//			[!CDATA[
//			select where salary > 30000
//			]]
//			</query>
			
		assertTrue(emps.size() == 4);
		for (Object o: emps) {
			Employee e = (Employee) o;
			assertTrue(e.getSalary() > 30000);
		}
		
		TestTools.closePM(pm);
	}
	
	/**
	 * 14.10.2 Basic query with ordering.
	 * This query selects all Employee instances from the candidate collection where the salary is 
	 * greater than the constant 30000, and returns a Collection ordered based on employee salary.
	 */
	@Test
	public void testQuery_14_10_2() {
		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();
		
		Query q = pm.newQuery (Employee.class, "salary > 30000");
		q.setOrdering ("salary ascending");
		Collection<Employee> emps = (Collection<Employee>) q.execute ();
//			<query name="ordering">
//			[!CDATA[
//			select where salary > 30000
//			order by salary ascending
//			]]
//			</query>

		assertTrue(emps.size() == 4);
		float prev = 30000;
		for (Object o: emps) {
			Employee e = (Employee) o;
			assertTrue(e.getSalary() >= prev);
			prev = e.getSalary();
		}

		TestTools.closePM(pm);
	}
	
	/**
	 * 14.10.3 Parameter passing.
	 * This query selects all Employee instances from the candidate collection where the salary is 
	 * greater than the value passed as a parameter and the name starts with the value passed as a 
	 * second parameter.
	 * If the value for the salary field in a candidate instance is null, then it cannot be 
	 * unwrapped for the comparison, and the candidate instance is rejected.
	 */
	@Test
	public void testQuery_14_10_3() {
		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();
	
		Query q = pm.newQuery (Employee.class,
		"salary > sal && name.startsWith(begin)");  //TODO type in spec: ")" was missing
		q.declareParameters ("Float sal, String begin");
		Collection emps = (Collection) q.execute (new Float (30000.));
        fail("TODO");
//			<query name="parameter">
//			[!CDATA[
//			select where salary > :sal && name.startsWith(:begin)
//			]]
//			</query>
			
			TestTools.closePM(pm);
	}
	
	/**
	 * 14.10.4 Navigation through single-valued field.
	 * This query selects all Employee instances from the candidate collection where the value of 
	 * the name field in the Department instance associated with the Employee instance is equal to 
	 * the value passed as a parameter.
	 * If the value for the dept field in a candidate instance is null, then it cannot be 
	 * navigated for the comparison, and the candidate instance is rejected.
	 */
	@Test
	public void testQuery_14_10_4() {
		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();

		Query q = pm.newQuery (Employee.class, "dept.name == dep");
		q.declareParameters ("String dep");
		String rnd = "R&D";
		Collection emps = (Collection) q.execute (rnd);
        fail("TODO");
//			<query name="navigate">
//			[!CDATA[
//			select where dept.name == :dep
//			]]
//			</query>
		
		TestTools.closePM(pm);
	}
	
	/**
	 * 14.10.5 Navigation through multi-valued field.
	 * This query selects all Department instances from the candidate collection where the 
	 * collection of Employee instances contains at least one Employee instance having a salary 
	 * greater than the value passed as a parameter.
	 */
	@Test
	public void testQuery_14_10_5() {
		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();

		String filter = "emps.contains (emp) & emp.salary > sal";
		Query q = pm.newQuery (Department.class, filter);
		q.declareParameters ("float sal");
		q.declareVariables ("Employee emp");
		Collection deps = (Collection) q.execute (new Float (30000.));
        fail("TODO");
//			<query name="multivalue">
//			[!CDATA[
//			select where emps.contains(e)
//			&& e.salary > :sal
//			]]
//			</query>
			
			TestTools.closePM(pm);
	}
	
	/**
	 * 14.10.6 Membership in a collection
	 * This query selects all Department instances where the name field is contained in a 
	 * parameter collection, which in this example consists of three department names.
	 */
	@Test
	public void testQuery_14_10_6() {
		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();

		String filter = "depts.contains(name)";
		Query q = pm.newQuery (Department.class, filter);
		List<String> depts =
			Arrays.asList(new String [] {"R&D", "Sales", "Marketing"});
		q.declareParameters ("Collection depts");
		Collection deps = (Collection) q.execute (depts);
        fail("TODO");
//			<query name="collection">
//			[!CDATA[
//			select where :depts.contains(name)
//			]]
//			</query>
		
		TestTools.closePM(pm);
	}
	
	/**
	 * 14.10.7 Projection of a Single Field
	 * This query selects names of all Employees who work in the parameter department.
	 */
	@Test
	public void testQuery_14_10_7() {
		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();
		
		Query q = pm.newQuery (Employee.class, "dept.name == deptName");
		q.declareParameters ("String deptName");
		q.setResult("name");
		Collection<String> names = (Collection<String>) q.execute("R&D");
		Iterator<String> it = names.iterator();
		int n = 0;
		while (it.hasNext()) {
			String name = it.next();
            fail("TODO");
			// ...
			n++;
		}
		assertEquals(7, n);
//			<query name="project">
//			[!CDATA[
//			select name where dept.name == :deptName
//			]]
//			</query>
			
			TestTools.closePM(pm);
	}
	
	/**
	 * 14.10.8 Projection of Multiple Fields and Expressions
	 * This query selects names, salaries, and bosses of Employees who work in the parameter 
	 * department.
	 * 
	 * <code>
	 * class Info {
	 * 		public String name;
	 * 		public Float salary;
	 * 		public Employee reportsTo;
	 * }
	 * </code>
	 */
	@Test
	public void testQuery_14_10_8() {
		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();

		Query q = pm.newQuery (Employee.class, "dept.name == deptName");
		q.declareParameters ("String deptName");
		q.setResult("name, salary, boss as reportsTo");
		q.setResultClass(Info.class);
		Collection<Info> names = (Collection<Info>) q.execute("R&D");
		Iterator<Info> it = names.iterator();
		while (it.hasNext()) {
			Info info = it.next();
			String name = info.name;
			Employee boss = info.reportsTo;
			// ...
			fail("TODO");
		}
//			<query name="resultclass">
//			[!CDATA[
//			select name, salary, boss as reportsTo into Info
//			where dept.name == :deptName
//			]]
//			</query>
		
		TestTools.closePM(pm);
	}
	
	/**
	 * 14.10.9 Projection of Multiple Fields and Expressions into a Constructed instance
	 * This query selects names, salaries, and bosses of Employees who work in the parameter 
	 * department, and uses the constructor for the result class.
	 * 
	 * <code>
	 * class Info {
	 * 		public String name;
	 * 		public Float salary;
	 * 		public Employee reportsTo;
	 * 		public Info (String name, Float salary, Employee reportsTo) {
	 * 			this.name = name;
	 * 			this.salary = salary;
	 * 			this.reportsTo = reportsTo;
	 * 		}
	 * }
	 * </code>
	 */
	@Test
	public void testQuery_14_10_9() {
		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();

		Query q = pm.newQuery (Employee.class, "dept.name == deptName");
		q.declareParameters ("String deptName");
		q.setResult("new Info(name, salary, boss)");
		q.setResultClass(Info.class);
		Collection<Info> names = (Collection<Info>) q.execute("R&D");
		Iterator<Info> it = names.iterator();
		while (it.hasNext()) {
			Info info = it.next();
			String name = info.name;
			Employee boss = info.reportsTo;
			//...
			fail("TODO");
		}
//			<query name="construct">
//			[!CDATA[
//			select new Info (name, salary, boss)
//			where dept.name == :deptName
//			]]
//			</query>
		
		TestTools.closePM(pm);
	}
	
	/**
	 * 14.10.10 Aggregation of a single Field
	 * This query averages the salaries of Employees who work in the parameter department and 
	 * returns a single value.
	 */
	@Test
	public void testQuery_14_10_10() {
		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();

		Query q = pm.newQuery (Employee.class, "dept.name == deptName");
		q.declareParameters ("String deptName");
		q.setResult("avg(salary)");
		Float avgSalary = (Float) q.execute("R&D");
        fail("TODO");
//			<query name="aggregate">
//			[!CDATA[
//			select avg(salary)
//			where dept.name == :deptName
//			]]
//			</query>
		
		TestTools.closePM(pm);
	}
	
	/**
	 * 14.10.11 Aggregation of Multiple Fields and Expressions
	 * This query averages and sums the salaries of Employees who work in the parameter department.
	 */
	@Test
	public void testQuery_14_10_11() {
		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();

		Query q = pm.newQuery (Employee.class, "dept.name == deptName");
		q.declareParameters ("String deptName");
		q.setResult("avg(salary), sum(salary)");
		Object[] avgSum = (Object[]) q.execute("R&D");
		Float average = (Float)avgSum[0];
		Float sum = (Float)avgSum[1];
        fail("TODO");
//			<query name="multiple">
//			[!CDATA[
//			select avg(salary), sum(salary)
//			where dept.name == :deptName
//			]]
//			</query>
		
		TestTools.closePM(pm);
	}
	
	/**
	 * 14.10.12 Aggregation of Multiple fields with Grouping
	 * This query averages and sums the salaries of Employees who work in all departments having 
	 * more than one employee and aggregates by department name.
	 */
	@Test
	public void testQuery_14_10_12() {
		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();

		Query q = pm.newQuery (Employee.class);
		q.setResult("avg(salary), sum(salary), dept.name");
		q.setGrouping("dept.name having count(dept.name) > 1");
		Collection<Object[]> results = (Collection<Object[]>)q.execute();
		Iterator<Object[]> it = results.iterator();
		while (it.hasNext()) {
			Object[] info = it.next();
			Float average = (Float)info[0];
			Float sum = (Float)info[1];
			String deptName = (String)info[2];
            fail("TODO");
            //...
		}
//			<query name="group">
//			[!CDATA[
//			select avg(salary), sum(salary), dept.name from com.xyz.hr.Employee where
//			dept.name == :deptName group by dept.name having count(dept.name) > 1
//			]]
//			</query>
		
		TestTools.closePM(pm);
	}
	
	/**
	 * 14.10.13 Selection of a Single Instance
	 * This query returns a single instance of Employee.
	 */
	@Test
	public void testQuery_14_10_13() {
		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();

		Query q = pm.newQuery (Employee.class, "name == empName");
		q.declareParameters ("String empName");
		q.setUnique(true);
		Employee emp = (Employee) q.execute("Michael");
        fail("TODO");
//			<query name="unique">
//			[!CDATA[
//			select unique this
//			where dept.name == :deptName
//			]]
//			</query>
		
		TestTools.closePM(pm);
	}
	
	/**
	 * 14.10.14 Selection of a Single Field
	 * This query returns a single field of a single Employee.
	 */
	@Test
	public void testQuery_14_10_14() {
		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();

		Query q = pm.newQuery (Employee.class, "name == empName");
		q.declareParameters ("String empName");
		q.setResult("salary");
		q.setResultClass(Float.class);
		q.setUnique(true);
		Float salary = (Float) q.execute ("Michael");
        fail("TODO");
//			<query name="single">
//			[!CDATA[
//			select unique new Float(salary)
//			where dept.name == :deptName
//			]]
//			</query>
		
		TestTools.closePM(pm);
	}
	
	/**
	 * 14.10.15 Projection of �this� to User-defined Result Class with Matching Field
	 * This query selects instances of Employee who make more than the parameter salary and stores 
	 * the result in a user-defined class. Since the default is �distinct this as Employee�, the 
	 * field must be named Employee and be of type Employee.
	 * 
	 * <code>
	 * class EmpWrapper {
	 * 		public Employee Employee;
	 * }
	 * </code>
	 */
	@Test
	public void testQuery_14_10_15() {
		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();

		Query q = pm.newQuery (Employee.class, "salary > sal");
		q.declareParameters ("Float sal");
		q.setResultClass(EmpWrapper.class);
		Collection<EmpWrapper> infos = (Collection<EmpWrapper>) q.execute (new Float (30000.));
		Iterator<EmpWrapper> it = infos.iterator();
		while (it.hasNext()) {
			EmpWrapper info = it.next();
			Employee e = info.Employee;
            fail("TODO");
            //...
		}
//			<query name="thisfield">
//			[!CDATA[
//			select into EmpWrapper
//			where salary > sal
//			]]
//			</query>
		
		TestTools.closePM(pm);
	}
	
	/**
	 * 14.10.16 Projection of �this� to User-defined Result Class with Matching Method
	 * This query selects instances of Employee who make more than the parameter salary and stores 
	 * the result in a user-defined class.
	 * 
	 * <code>
	 * class EmpInfo {
	 * 		private Employee worker;
	 * 		public Employee getWorker() {return worker;}
	 * 		public void setEmployee(Employee e) {worker = e;}
	 * }
	 * </code>
	 */
	@Test
	public void testQuery_14_10_16() {
		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();

		Query q = pm.newQuery (Employee.class, "salary > sal");
		q.declareParameters ("Float sal");
		q.setResultClass(EmpInfo.class);
		Collection<EmpInfo> infos = (Collection<EmpInfo>) q.execute (new Float (30000.));
		Iterator<EmpInfo> it = infos.iterator();
		while (it.hasNext()) {
			EmpInfo info = it.next();
			Employee e = info.getWorker();
            fail("TODO");
            //...
		}
//			<query name="thismethod">
//			[!CDATA[
//			select into EmpInfo
//			where salary > sal
//			]]
//			</query>
		
		TestTools.closePM(pm);
	}
	
	/**
	 * 14.10.17 Projection of variables
	 * This query returns the names of all Employees of all "Research" departments:
	 */
	@Test
	public void testQuery_14_10_17() {
		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();

		Query q = pm.newQuery(Department.class);
		q.declareVariables("Employee e");
		q.setFilter("name.startsWith('Research') && emps.contains(e)");
		q.setResult("e.name");
		Collection<String> names = (Collection<String>) q.execute();
		Iterator<String> it = names.iterator();
		while (it.hasNext()) {
			String name = it.next();
	         fail("TODO");
	         //...
		}
//			<query name="variables">
//			[!CDATA[
//			select e.name
//			where name.startsWith('Research')
//			&& emps.contains((com.xyz.hr.Employee) e)
//			]]
//			</query>
		
		TestTools.closePM(pm);
	}
	
	/**
	 * 14.10.18 Non-correlated subquery
	 * This query returns names of employees who work more than the average of all employees:
	 * 
	 * Single string form.
	 */
	@Test
	public void testQuery_14_10_18a() {
		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();

		// single string form
		Query q = pm.newQuery(
			"select name from com.xyz.hr.Employee "+
			"where this.weeklyhours > " +
			"(select avg(e.weeklyhours) from com.xyz.hr.Employee e)");
		Collection<String> names = (Collection<String>) q.execute();
		Iterator<String> it = names.iterator();
		while (it.hasNext()) {
			String name = it.next();
            fail("TODO");
            //...
		}
		
		TestTools.closePM(pm);
	}
	
	/**
	 * 14.10.18 Non-correlated subquery
	 * This query returns names of employees who work more than the average of all employees:
	 * 
	 * Subquery instance form.
	 */
	@Test
	public void testQuery_14_10_18b() {
		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();

		// subquery instance form
		Query subq = pm.newQuery(Employee.class);
		subq.setFilter("select avg(weeklyhours)");
		Query q = pm.newQuery(Employee.class);
		q.setFilter("this.weeklyhours > average_hours");
		q.setResult("this.name");
//TODO not in standard!!!		q.setSubquery(subq, "double average_hours", null);
		Collection<String> names = (Collection<String>) q.execute();
		Iterator<String> it = names.iterator();
		while (it.hasNext()) {
			String name = it.next();
            fail("TODO");
            //...
		}
//			<query name="noncorrelated_subquery">
//			[!CDATA[
//			select name from com.xyz.hr.Employee
//			where this.weeklyhours >
//			(select avg(e.weeklyhours) from com.xyz.hr.Employee e)
//			]]
//			</query>
		
		TestTools.closePM(pm);
	}
	
	/**
	 * 14.10.19 Correlated subquery
	 * This query returns names of employees who work more than the average of employees in the 
	 * same department having the same manager. The candidate collection of the subquery is the 
	 * collection of employees in the department of the candidate employee and the parameter 
	 * passed to the subquery is the manager of the candidate employee.
	 * 
	 * Single string form.
	 */
	@Test
	public void testQuery_14_10_19a() {
		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();

		// single string form
		Query q = pm.newQuery(
			"select name from com.xyz.hr.Employee "+
			"where this.weeklyhours > " +
			"(select AVG(e.weeklyhours) from this.department.employees as e " +
			"where e.manager == this.manager)");
		Collection<String> names = (Collection<String>) q.execute();
		Iterator<String> it = names.iterator();
		while (it.hasNext()) {
			String name = it.next();
            fail("TODO");
            //...
		}
		
		TestTools.closePM(pm);
	}
	
	/**
	 * 14.10.19 Correlated subquery
	 * This query returns names of employees who work more than the average of employees in the 
	 * same department having the same manager. The candidate collection of the subquery is the 
	 * collection of employees in the department of the candidate employee and the parameter 
	 * passed to the subquery is the manager of the candidate employee.
	 * 
	 * Subquery instance form.
	 */
	@Test
	public void testQuery_14_10_19b() {
		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();

		// subquery instance form
		Query subq = pm.newQuery(Employee.class);
		subq.setFilter("this.manager == :manager");
		subq.setResult("avg(weeklyhours)");
		Query q = pm.newQuery(Employee.class);
		q.setFilter("this.weeklyhours > average_hours");
		q.setResult("name");
		//TODO not in standard!!!		q.setSubquery(subq, "double average_hours","department.employees",
//			"this.manager");
		Collection<String> names = (Collection<String>) q.execute();
		Iterator<String> it = names.iterator();
		while (it.hasNext()) {
			String name = it.next();
            fail("TODO");
            //...
		}
//			<query name="correlated_subquery">
//			[!CDATA[
//			select name from com.xyz.hr.Employee
//			where this.weeklyhours >
//			(select AVG(e.weeklyhours) from this.department.employees e
//			where e.manager == this.manager)
//			]]
//			</query>
		
		TestTools.closePM(pm);
	}
	
	/**
	 * 14.10.20 Deleting Multiple Instances
	 * This query deletes all Employees who make more than the parameter salary.
	 */
	@Test
	public void testQuery_14_10_20() {
		PersistenceManager pm = TestTools.openPM();
		pm.currentTransaction().begin();

		Query q = pm.newQuery (Employee.class, "salary > sal");
		q.declareParameters ("Float sal");
		q.deletePersistentAll(new Float(30000.));
		//TODO check deletion!
        fail("TODO");
		TestTools.closePM(pm);
	}

	@AfterClass
	public static void tearDown() {
		TestTools.removeDb(DB_NAME);
	}
	
}
