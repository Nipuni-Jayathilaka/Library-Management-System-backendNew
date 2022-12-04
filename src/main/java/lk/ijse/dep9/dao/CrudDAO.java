package lk.ijse.dep9.dao;

import lk.ijse.dep9.dao.custom.exception.ConstraintViolationException;
import lk.ijse.dep9.entity.SuperEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

                                    //one class and many interface
public interface CrudDAO<T extends SuperEntity ,ID extends Serializable> extends SuperDAO{//id is bounded type here
    // for T we can only put what is inside the entity
    public long count();
    public void deleteById(ID id) throws ConstraintViolationException;
    public boolean existsById(ID pk);
    public List<T> findAll();
    public Optional<T> findById(ID pk);
    public Object save(T entity) throws ConstraintViolationException;
    public Object update(T entity) throws ConstraintViolationException;

}
