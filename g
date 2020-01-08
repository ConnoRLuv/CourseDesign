S' -> S
S -> C S
        | P S
        | L S
        | M {emit(M.expr)}
        | S S
C -> if ( boolean ) {
P -> C M else  { emit("if boolean.expr goto --" ) emit("goto --")}
M -> { L S }
        | L {M.expr = L.expr, emit(L.cnt)}
        | { S } {M.expr = S.expr, emit(S.cnt)}
L ->  A { L.cnt = 1, L.expr = A.expr}
        | L L {L.cnt = L1.cnt + L2. cnt,L.expr = L1.expr + L2.expr}
boolean -> E <关系运算符> E { boolean.expr = E1.expr + <关系运算符>.val + E2.expr}
            | <布尔值>  {boolean.val = <布尔值>.val}
A -> <普通标识符> = E ; {emit(A.expr = <普通标识符>.val + = + E.val)}
E -> E + T    {E.expr = E1.expr + + + T.expr}
        | T   {E.expr = T.expr}
        | E - T {E.expr = E1.expr + - + T.expr}
T -> T * F {T.expr = T1.expr + * + F.expr}
        | F  {T.expr = F.expr }
        | T / F {T.expr = T1.expr + / + F.expr}
F -> <普通标识符> {F.expr = <普通标识符>.val }
        | <整数>  {F.expr = <整数>.val }
        | <浮点数>  {F.expr = <浮点数>.val }
        | ( E )  {F.expr = E.expr }
