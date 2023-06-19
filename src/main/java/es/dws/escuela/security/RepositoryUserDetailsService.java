package es.dws.escuela.security;

import es.dws.escuela.entities.Teacher;
import es.dws.escuela.entities.User;
import es.dws.escuela.repositories.TeacherRepository;
import es.dws.escuela.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RepositoryUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> op = userRepository.findById(username);
        if(op.isPresent()){
            User user = op.get();
            List<GrantedAuthority> roles = new ArrayList<>();
            for (String role : user.getRoles()) {
                roles.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
            return new org.springframework.security.core.userdetails.User(user.getId(),
                    user.getPass(), roles);
        }else {
            Teacher teacher = teacherRepository.findById(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            List<GrantedAuthority> roles = new ArrayList<>();
            for (String role : teacher.getRoles()) {
                roles.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
            return new org.springframework.security.core.userdetails.User(teacher.getId(),
                    teacher.getPass(), roles);

        }


    }
}
