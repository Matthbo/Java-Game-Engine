package matthbo.game.engine.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

public class InputHandler implements KeyListener {

    private KeyEnum[] keys = new KeyEnum[((1 << 16) - 1)];

    public InputHandler(){
        Arrays.fill(keys, KeyEnum.UNTOUCHED);

        keys[1] = KeyEnum.RELEASED;
    }

    public boolean isTypedKey(int key){
        return keys[key] == KeyEnum.TYPED;
    }

    public boolean isPressedKey(int key){
        return keys[key] == KeyEnum.PRESSED;
    }

    public boolean isReleasedKey(int key){
        return keys[key] == KeyEnum.RELEASED;
    }

    public void clear(){
        for (int i = 0; i < keys.length; i++) {
            if(keys[i] == KeyEnum.RELEASED || keys[i] == KeyEnum.TYPED) keys[i] = KeyEnum.UNTOUCHED;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        keys[e.getKeyCode()] = KeyEnum.TYPED;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = KeyEnum.PRESSED;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = KeyEnum.RELEASED;
    }

    private enum KeyEnum {
        UNTOUCHED, PRESSED, RELEASED, TYPED
    }
}
