package com.urise.webapp.storage;

import com.urise.webapp.exception.ExistStorageException;
import com.urise.webapp.exception.NotExistStorageException;
import com.urise.webapp.model.Resume;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractStorage<T> implements Storage {

    protected static final Comparator<Resume> NAME_COMPARATOR =
            Comparator.comparing(Resume::getFullName).thenComparing(Resume::getUuid);
    /*(o1, o2) -> {
        if (o1.getFullName().compareTo(o2.getFullName()) == 0){
            return o1.getUuid().compareTo(o2.getUuid());
        } else {
            return o1.getFullName().compareTo(o2.getFullName());
        }
    };*/

    @Override
    public void update(Resume resume) {
        T searchKey = getSearchKeyIfExist(resume.getUuid());
        updateInStorage(resume, searchKey);
    }

    @Override
    public void save(Resume resume) {
        try {
            getSearchKeyIfExist(resume.getUuid());
        } catch (NotExistStorageException ex) {
            saveInStorage(resume, findResumeKey(resume.getUuid()));
            return;
        }
        throw new ExistStorageException(resume.getUuid());
    }

    @Override
    public Resume get(String uuid) {
        T searchKey = getSearchKeyIfExist(uuid);
        return getFromStorage(searchKey);
    }

    @Override
    public void delete(String uuid) {
        T searchKey = getSearchKeyIfExist(uuid);
        deleteFromStorage(searchKey);
    }

    private T getSearchKeyIfExist(String uuid) {
        T searchKey = findResumeKey(uuid);
        if (!isKeyExist(searchKey)){
            throw new NotExistStorageException(uuid);
        }
        return searchKey;
    }

    public List<Resume> getAllSorted() {
        Resume[] tempArray = getAll();
        Arrays.sort(tempArray, NAME_COMPARATOR);
        return Arrays.asList(tempArray);
    }

    protected abstract T findResumeKey(String uuid);

    protected abstract boolean isKeyExist(T checkKey);

    protected abstract void updateInStorage(Resume resume, T searchKey);

    protected abstract void saveInStorage(Resume resume, T searchKey);

    protected abstract Resume getFromStorage(T searchKey);

    protected abstract void deleteFromStorage(T searchKey);

    protected abstract Resume[] getAll();
}
