import { FormControl } from '@angular/forms';
import { integerValidator } from './integer-validator';

describe('integerValidator', () => {
    it('should return null if control value is null', () => {
        const control = new FormControl(null);
        expect(integerValidator(control)).toBeNull();
    });

    it('should return null if control value is undefined', () => {
        const control = new FormControl(undefined);
        expect(integerValidator(control)).toBeNull();
    });

    it('should return null if value is a valid integer', () => {
        const control = new FormControl(42);
        expect(integerValidator(control)).toBeNull();
    });

    it('should return null if value is zero', () => {
        const control = new FormControl(0);
        expect(integerValidator(control)).toBeNull();
    });

    it('should return null if value is a negative integer', () => {
        const control = new FormControl(-10);
        expect(integerValidator(control)).toBeNull();
    });

    it('should return { integer: true } if value is a float', () => {
        const control = new FormControl(3.14);
        expect(integerValidator(control)).toEqual({ integer: true });
    });

    it('should return { integer: true } if value is a string ("123")', () => {
        const control = new FormControl('123');
        expect(integerValidator(control)).toEqual({ integer: true });
    });

    it('should return { integer: true } if value is NaN', () => {
        const control = new FormControl(NaN);
        expect(integerValidator(control)).toEqual({ integer: true });
    });
});
