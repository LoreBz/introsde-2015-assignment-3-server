package introsde.assignment.soap;

import introsde.document.model.HealthMeasureHistory;
import introsde.document.model.LifeStatus;
import introsde.document.model.MeasureDefinition;
import introsde.document.model.Person;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;

//Service Implementation
@WebService(endpointInterface = "introsde.assignment.soap.People", serviceName = "PeopleService")
public class PeopleImpl implements People {

	@Override
	public Person readPerson(Long id) {
		System.out.println("---> Reading Person by id = " + id);
		Person p = Person.getPersonById((int) (long) id);
		if (p != null) {
			System.out.println("---> Found Person by id = " + id + " => "
					+ p.getName());
		} else {
			System.out.println("---> Didn't find any Person with  id = " + id);
		}
		return p;
	}

	@Override
	public Person updatePerson(Person person) {
		return Person.updatePerson(person);
	}

	@Override
	public boolean deletePerson(Long id) {
		Person p = Person.getPersonById((int) (long) id);
		if (p != null) {
			Person.removePerson(p);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<HealthMeasureHistory> readPersonHistory(Long id,
			String measureName) {
		return this.getHMhistoryByUserAndType(id, measureName);
	}

	@Override
	public List<MeasureDefinition> readMeasureTypes() {
		return MeasureDefinition.getAll();
	}

	@Override
	public HealthMeasureHistory readPersonMeasure(Long personId,
			String measureName, Long mid) {
		List<HealthMeasureHistory> all = this.getHMhistoryByUserAndType(
				personId, measureName);
		if (all == null || all.isEmpty()) {
			return null;
		} else {
			for (HealthMeasureHistory hm : all) {
				if (hm.getIdMeasureHistory() == mid) {
					return hm;
				}
			}
			return null;
		}
	}

	@Override
	public LifeStatus savePersonMeasure(Long personId, LifeStatus measure) {
		Person existingPerson = Person.getPersonById((int) (long) personId);
		if (existingPerson == null) {
			return null;
		}
		String measureType = measure.getMeasureDefinition().getMeasureName();
		System.out.println("personID valid");
		System.out
				.println("getting measuredefinition with type=" + measureType);
		MeasureDefinition md = null;
		try {
			List<MeasureDefinition> mdlist = MeasureDefinition.getAll();
			for (MeasureDefinition measureDefinition : mdlist) {
				if (measureDefinition.getMeasureName().equals(measureType)) {
					md = measureDefinition;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("errore nel prendere la measuredef");
		}
		if (measure.getMeasureDefinition() != null) {
			if (!measure.getMeasureDefinition().getMeasureName()
					.equals(md.getMeasureName())) {
				System.out.println("You set a path with measuretypeid="
						+ measureType + " that means=" + md.getMeasureName()
						+ "\nbut you set in the body a measuredef="
						+ measure.getMeasureDefinition().getMeasureName());
			}
		}
		// to update lifestatus we update the lifestatus with the same
		// measuretype
		LifeStatus existingLS = this.getLifestatusByPersonIdAndMeasureType(
				personId, md.getMeasureName());
		if (existingLS != null) {
			System.out.println("removing old lifestatus");
			LifeStatus.removeLifeStatus(existingLS);
		}
		System.out.println("preparing new lifestatus");
		LifeStatus toSave = new LifeStatus();
		toSave.setMeasureDefinition(md);
		toSave.setPerson(existingPerson);
		toSave.setValue(measure.getValue());
		LifeStatus.saveLifeStatus(toSave);

		// and put the record in the history too
		HealthMeasureHistory hm = new HealthMeasureHistory();
		hm.setPerson(existingPerson);
		hm.setMeasureDefinition(md);
		hm.setValue(measure.getValue());
		hm.setTimestamp(new Date(System.currentTimeMillis()));
		System.out.println("saving new lifestatus");
		HealthMeasureHistory.saveHealthMeasureHistory(hm);
		System.out.println("end");

		return toSave;
	}

	@Override
	public HealthMeasureHistory updatePersonMeasure(Long id,
			HealthMeasureHistory hm) {
		HealthMeasureHistory retrievedHM = HealthMeasureHistory
				.getHealthMeasureHistoryById(hm.getIdMeasureHistory());
		if (id != retrievedHM.getPerson().getIdPerson()) {
			return HealthMeasureHistory.updateHealthMeasureHistory(hm);
		}
		return null;
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

	LifeStatus getLifestatusByPersonIdAndMeasureType(Long personID,
			String measurename) {
		List<LifeStatus> list = LifeStatus.getAll();
		for (LifeStatus ls : list) {
			if (ls.getPerson().getIdPerson() == personID
					&& ls.getMeasureDefinition().getMeasureName()
							.equals(measurename)) {
				return ls;
			}
		}
		return null;
	}

	@Override
	public Person createPerson(Person person) {
		return Person.savePerson(person);
	}

	@Override
	public List<Person> getPeople() {
		return Person.getAll();
	}

}
