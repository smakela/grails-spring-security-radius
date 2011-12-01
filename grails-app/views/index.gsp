<!doctype html>
<html>
    <head>
    </head>
    <body>
        <div>
            Hello <sec:loggedInUserInfo field="username"/>!<br/>
            <sec:ifAllGranted roles="ROLE_USER">You have role user<br/></sec:ifAllGranted>
            <g:link controller='logout'>Logout</g:link>
        </div>
    </body>
</html>