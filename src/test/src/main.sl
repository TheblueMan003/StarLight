package test

struct Dictionnary<K, V>{
    json data

    def __init__(){
        this.data = {}
    }

    def macro V get(K key){
        return this.data["$(key)"]
    }
    def set(K key, V value){
        def macro inner(K a){
            this.data["$(a)"] = value
        }
        inner(key)
    }
}

Dictionnary<int, int> d = new Dictionnary<int, int>()

def test(){
    d.set(1, 42)
    int i = d.get(1)
}