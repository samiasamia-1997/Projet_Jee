package org.mrb.authservice.sec.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mrb.authservice.sec.JwtUtil;
import org.mrb.authservice.sec.entities.AppUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtAthenticationFilter extends UsernamePasswordAuthenticationFilter {
    //on va créer deux méthodes de filtre: attemtAuthentication et successfulAuthentication
   private AuthenticationManager authenticationManager;

    public JwtAthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    //cette méthode va etre exécuter quand le user entre son username et son mot de passe
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username=request.getParameter("username");
        String password=request.getParameter("password");
        System.out.println("attemptAuthentication");
        System.out.println(username);
        System.out.println(password);
       /*
        try {
            AppUser appUser=new ObjectMapper().readValue(request.getInputStream(),AppUser.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username,password);
        return authenticationManager.authenticate(authenticationToken);
    }

//cette méthode va éxécuter une fois spring security va aller vers bdd et trouve les données correctes
    //la ou on génere le JWT , on a besoin de la library : auth0 jwt maven
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication");

        User user=(User) authResult.getPrincipal();
        //jwt est constitué de trois parties  : payload , header , signature
        //on cree un algo pour la signature de jwt , on chosit soit HMAC ou RSA
        Algorithm algorithm=Algorithm.HMAC256(JwtUtil.SECRET);//signature
        String jwtAccessToken= JWT.create()//paylod
                .withSubject(user.getUsername())
                .withIssuer(request.getRequestURL().toString())
                .withExpiresAt(new Date(System.currentTimeMillis()+JwtUtil.ACCESS_TOKEN_TIMEOUT))
                .withClaim("roles",user.getAuthorities().stream().map(ga->ga.getAuthority()).collect(Collectors.toList()))
                .sign(algorithm);
        //refresh Token : token qui permet d'avoir un nouveau Access Token
        String jwtRefreshToken= JWT.create()
                .withSubject(user.getUsername())
                .withIssuer(request.getRequestURL().toString())
                .withExpiresAt(new Date(System.currentTimeMillis()+JwtUtil.REFRESH_TOKEN_TIMEOUT))
                .sign(algorithm);
        System.out.println(jwtAccessToken);
        Map<String,String> idToken=new HashMap<>();
        idToken.put("access-token",jwtAccessToken);
        idToken.put("refresh-token",jwtRefreshToken);
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(),idToken);

    }

}

