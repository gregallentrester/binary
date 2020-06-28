<#-- @ftlvariable name="_csrf" type="org.springframework.security.web.csrf.CsrfToken" -->
<#-- @ftlvariable name="form" type="eu.kielczewski.example.domain.UserCreateForm" -->
<#import "/spring.ftl" as spring>

<!DOCTYPE html>
<html lang="en">
<head<meta charset="utf-8"><title>Create a new user</title></head>

<body>
  <nav role="navigation">
  <ul>
    <li><a href="/">Home</a>
    </ul>
  </nav>

  <h4>Create a new user</h4>

  <form role="form" name="form" action="" method="post">

    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

    <div>
      <label for="email">Email address</label>
      <#if (form.email)??>
        <!--input type="email" name="email" id="email" value="${form.email}" required autofocus/-->
        <input type="email" name="email" id="email" value="demo@localhost" required autofocus/>
      </#if>
    </div>
    <div>
      <label for="password">Password</label>
      <#if (form.password)??>
        <!--input type="password" name="password" id="password" required/-->
        <input type="password" name="password" id="password"  value="demo" required/>
      </#if>
    </div>
    <div>
      <label for="passwordRepeated">Repeat</label>
      <#if (form.passwordRepeated)??>
        <input type="password" name="passwordRepeated" id="passwordRepeated" required/>
      </#if>
    </div>
    <div>


      <label for="role">Role</label>
    <#assign role = "USER" />
      <select name="role" id="role" required>

        <#if (form.role)??>
          <option <#if form.role == 'USER'>selected</#if>>USER</option>
        </#if>
        <#if (form.role)??>
          <option <#if form.role == 'ADMIN'>selected</#if>>ADMIN</option>
        </#if>
        <option
      </select>
    </div>

    <button type="submit">Save</button>
  </form>

  <@spring.bind "form" />

  <#if spring.status.error>
  <ul>
    <#list spring.status.errorMessages as error>
      <li>${error}</li>
    </#list>
  </ul>
  </#if>

</body></html>
