package introsde.document.test;

import introsde.document.model.HealthMeasureHistory;
import introsde.document.model.Person;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MyTestClass {

	// @Test
	public void readPerson() {
		Long id = new Long(1);
		System.out.println("---> Reading Person by id = " + id);
		Person p = Person.getPersonById((int) (long) id);
		if (p != null) {
			System.out.println("---> Found Person by id = " + id + " => "
					+ p.getName());
		} else {
			System.out.println("---> Didn't find any Person with  id = " + id);
		}
	}

	// @Test
	public void t1() {
		Person newPerson = new Person();
		newPerson.setName("CIao");
		newPerson.setBirthdate(new Date());
		Person saved = Person.savePerson(newPerson);
		System.out.println(saved.toString());
	}

	// @Test
	public void t2() {
		Person ret = Person.getPersonById(2003);
		System.out.println(ret.toString());
	}

	//@Test
	public void t3() {
		List<Person> list = Person.getAll();
		for (Person p : list) {
			System.out.println(p.toString());
		}
	}

	@Test
	public void t4() {
		List<HealthMeasureHistory> list = getHMhistoryByUserAndType(
				new Long(1), "weight");
		System.out.println(list.toString());
	}

	private List<HealthMeasureHistory> getHMhistoryByUserAndType(Long id2,
			String measureType) {
		System.out.println("Reading HMhistory from DB for person with id: "
				+ id2 + " and measuretype of type=" + measureType);

		// this will work within a Java EE container, where not DAO will be
		// needed
		// Person person = entityManager.find(Person.class, personId);

		List<HealthMeasureHistory> hMhistoryList = HealthMeasureHistory
				.getAll();
		System.out.println("Got " + hMhistoryList.size() + " records");
		List<HealthMeasureHistory> retval = new ArrayList<>();
		for (HealthMeasureHistory hm : hMhistoryList) {
			if (hm.getPerson() != null) {
				if (hm.getPerson().getName() != null) {
					System.out.println(hm.getPerson().getName());
				}
				int idperson = hm.getPerson().getIdPerson();
				if (id2 == idperson) {
					if (hm.getMeasureDefinition() != null) {
						String measurename = hm.getMeasureDefinition()
								.getMeasureName();
						if (measurename.equals(measureType)) {
							retval.add(hm);
							System.out.println(hm.getPerson().getName()
									+ " "
									+ hm.getValue()
									+ " "
									+ hm.getMeasureDefinition()
											.getMeasureName());
						}
					} else {
						System.out.println("no measurdef! for this record");
					}
				}
			}

		}
		return retval;
	}

	@BeforeClass
	public static void beforeClass() {
		emf = Persistence.createEntityManagerFactory("introsde-jpa");
		em = emf.createEntityManager();
	}

	@AfterClass
	public static void afterClass() {
		em.close();
		emf.close();
	}

	@Before
	public void before() {
		tx = em.getTransaction();
	}

	private static EntityManagerFactory emf;
	private static EntityManager em;
	private EntityTransaction tx;
}
