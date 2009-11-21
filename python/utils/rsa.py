def gcd(a, b):
    if a > b:
        a, b = b, a
    if b % a == 0:
        return a
    return gcd(b % a, a)

p = int(raw_input("Input tow primes:"))
q = int(raw_input())
n = p * q
s = (p-1) * (q-1)
for e in range(2, n):
    if gcd(e, s) == 1:break

for d in range(2, n):
    if e * d % s == 1:break

print "p q n s e d\n", p, q, n, s, e, d

while True:
    m = int(raw_input())
    if m == 0: break
    c = m ** e % n
    print "After encrypt:", c
    print "After decrypt:", c ** d %n
    
