To verify CORS is working use:
```bash
http  http://localhost:8080/json 'Origin:http://example.com'
```
or 
```bash
curl -H "Origin: http://example.com" --verbose http://localhost:8080/json
```