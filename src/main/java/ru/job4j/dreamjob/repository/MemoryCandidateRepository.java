package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import javax.annotation.concurrent.ThreadSafe;
import java.time.LocalDateTime;
import java.util.*;

@ThreadSafe
@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private int nextId = 1;

    private final Map<Integer, Candidate> candidates = new HashMap<>();

    private MemoryCandidateRepository() {
        save(new Candidate(0, "Candidate 1", "description1", LocalDateTime.now(), false, 1, 0));
        save(new Candidate(0, "Candidate 2", "description2", LocalDateTime.now(), false, 1, 0));
        save(new Candidate(0, "Candidate 3", "description3", LocalDateTime.now(), false, 1, 0));
        save(new Candidate(0, "Candidate 4", "description4", LocalDateTime.now(), false, 1, 0));
        save(new Candidate(0, "Candidate 5", "description5", LocalDateTime.now(), false, 1, 0));

    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId++);
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
         return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(), (id, oldVacancy) -> {
            return new Candidate(
                    oldVacancy.getId(), candidate.getName(), candidate.getDescription(),
                    candidate.getCreationDate(), candidate.getVisible(), candidate.getCityId(), candidate.getFileId()
            );
        }) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
