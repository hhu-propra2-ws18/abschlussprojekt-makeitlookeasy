package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Case;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class FakeCaseRepository implements CaseRepository {
	private ArrayList<Case> cases;
	private Long id = 0L;

	public FakeCaseRepository(){
		cases = new ArrayList<Case>();
	}

	@Override
	public <S extends Case> S save(S entity) {
		if(entity.getId() == null){
			entity.setId(id);
			id++;
			cases.add(entity);
		}
		else{
			int index = entity.getId().intValue();
			cases.remove(index);
			cases.add(index, entity);
		}

		return null;
	}

	@Override
	public <S extends Case> Iterable<S> saveAll(Iterable<S> entities) {
		return null;
	}

	@Override
	public Optional<Case> findById(Long aLong) {
		return Optional.empty();
	}

	@Override
	public boolean existsById(Long aLong) {
		return false;
	}


	@Override
	public Iterable<Case> findAllById(Iterable<Long> longs) {
		return null;
	}

	@Override
	public long count() {
		return 0;
	}

	@Override
	public void deleteById(Long aLong) {

	}

	@Override
	public void delete(Case entity) {

	}

	@Override
	public void deleteAll(Iterable<? extends Case> entities) {

	}

	@Override
	public void deleteAll() {

	}

	@Override
	public ArrayList<Case> findAll() {
		return cases;
	}

	public ArrayList<Case> findByOwner(Long id){
		return cases.stream()
				.filter(c -> c.getOwner().equals(id))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public ArrayList<Case> findByReceiver(Long id) {
		return cases.stream()
				.filter(c -> c.getReceiver().equals(id))
				.collect(Collectors.toCollection(ArrayList::new));
	}
}
