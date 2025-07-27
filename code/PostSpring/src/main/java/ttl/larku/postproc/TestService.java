package ttl.larku.postproc;

import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Student;

public class TestService {

    private BaseDAO<Student> dao;

    //This annotation is handled in OurPostProc
    @OurInject
    public void setDao(BaseDAO<Student> dao) {
        System.out.println("TestService.setDao with " + dao);
        this.dao = dao;
    }

    public BaseDAO<Student> getDao() {
        return dao;
    }


}
